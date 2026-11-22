package me.funkel.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import me.funkel.media.AudioTrack;
import me.funkel.playlists.Playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Popup window to collect local audio files (file chooser or drag & drop)
 * and add them to a playlist.
 */
public class AddTracksDialog {

    private final Stage stage = new Stage();
    private final Playlist playlist;
    private final Runnable onChanged;

    private final List<File> files = new ArrayList<>();
    private final VBox fileList = new VBox(6);
    private final Button addButton = UI.flatButton("Add", Icon.ADD);

    /**
     * Opens the dialog. {@code onChanged} runs after the songs were added and again
     * whenever the tags (title, artist, cover...) of a new song have been read.
     */
    public static void show(Window owner, Playlist playlist, Runnable onChanged) {
        new AddTracksDialog(owner, playlist, onChanged).stage.show();
    }

    private AddTracksDialog(Window owner, Playlist playlist, Runnable onChanged) {
        this.playlist = playlist;
        this.onChanged = onChanged;

        VBox root = new VBox(14);
        root.setPadding(new Insets(24));
        root.setStyle(Theme.DIALOG_STYLE);

        Label header = UI.header("Add songs to " + playlist.getName());
        header.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(fileList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(Theme.TRANSPARENT_SCROLL_STYLE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, createDropZone(), scrollPane, createButtonRow());
        refreshFileList();

        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Add songs");
        stage.setScene(new Scene(root, 480, 540));
    }

    private VBox createDropZone() {
        Label hint = UI.label("Drop audio files here", 16, Theme.TEXT_SECONDARY);
        Label formats = UI.label(String.join(", ", FileDialogs.AUDIO_EXTENSIONS).toUpperCase(), 12, Theme.TEXT_FAINT);
        Button chooseButton = UI.flatButton("Choose files", Icon.ADD);
        chooseButton.setMaxWidth(Region.USE_PREF_SIZE);
        chooseButton.setOnAction(e -> addFiles(FileDialogs.chooseAudioFiles(stage)));

        VBox zone = new VBox(10, Icon.MUSIC_NOTE.create(36, Theme.TEXT_FAINT), hint, formats, chooseButton);
        zone.setAlignment(Pos.CENTER);
        zone.setPadding(new Insets(24));
        zone.setStyle(Theme.DROP_ZONE_STYLE);

        zone.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
                zone.setStyle(Theme.DROP_ZONE_ACTIVE_STYLE);
            }
            e.consume();
        });
        zone.setOnDragExited(e -> zone.setStyle(Theme.DROP_ZONE_STYLE));
        zone.setOnDragDropped(e -> {
            boolean hasFiles = e.getDragboard().hasFiles();
            if (hasFiles) {
                addFiles(e.getDragboard().getFiles());
            }
            e.setDropCompleted(hasFiles);
            e.consume();
        });
        return zone;
    }

    private HBox createButtonRow() {
        Button cancelButton = UI.flatButton("Cancel", Icon.CLOSE);
        cancelButton.setOnAction(e -> stage.close());
        addButton.setOnAction(e -> addToPlaylist());
        cancelButton.setPrefWidth(130);
        addButton.setPrefWidth(130);

        HBox row = new HBox(10, UI.hSpacer(), cancelButton, addButton);
        row.setAlignment(Pos.CENTER_RIGHT);
        return row;
    }

    // ---------- File list ----------

    private void addFiles(List<File> newFiles) {
        for (File file : newFiles) {
            if (FileDialogs.isAudioFile(file) && !files.contains(file)) {
                files.add(file);
            }
        }
        refreshFileList();
    }

    private void refreshFileList() {
        fileList.getChildren().clear();
        if (files.isEmpty()) {
            fileList.getChildren().add(UI.label("No files chosen yet.", 14, Theme.TEXT_FAINT));
        }
        for (File file : files) {
            fileList.getChildren().add(createFileRow(file));
        }
        addButton.setDisable(files.isEmpty());
        addButton.setText(files.isEmpty() ? "Add" : "Add " + files.size());
    }

    private HBox createFileRow(File file) {
        Label name = UI.label(file.getName(), 14, Theme.TEXT);
        name.setMinWidth(0);
        Label size = UI.label(Formats.fileSize(file.length()), 13, Theme.TEXT_FAINT);
        size.setMinWidth(Region.USE_PREF_SIZE);

        Button remove = UI.iconButton(Icon.CLOSE, 10, Theme.MARKER_COLOR);
        remove.setOnAction(e -> {
            files.remove(file);
            refreshFileList();
        });

        HBox row = new HBox(10, Icon.MUSIC_NOTE.create(16, Theme.TEXT_FAINT), name, UI.hSpacer(), size, remove);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(2, 4, 2, 12));
        row.setStyle(Theme.TRACK_ROW_STYLE);
        return row;
    }

    // ---------- Adding ----------

    private void addToPlaylist() {
        List<AudioTrack> newTracks = files.stream().map(AudioTrack::new).toList();
        playlist.getTracks().addAll(newTracks);
        stage.close();
        onChanged.run();
        AudioTrack.loadMetadata(newTracks, onChanged);
    }
}
