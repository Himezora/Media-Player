package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import me.funkel.Client;
import me.funkel.media.AudioTrack;
import me.funkel.playlists.Playlist;
import me.funkel.ui.AddTracksDialog;
import me.funkel.ui.CoverView;
import me.funkel.ui.FileDialogs;
import me.funkel.ui.Formats;
import me.funkel.ui.Icon;
import me.funkel.ui.NowPlayingBar;
import me.funkel.ui.Theme;
import me.funkel.ui.UI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistScene {

    private static final double PLAYLIST_COVER_SIZE = 52;
    private static final double TRACK_COVER_SIZE = 38;
    private static final double TRACK_INDENT = 28;

    private final SceneManager sceneManager;
    private final HBox outer = new HBox();
    private final VBox playlistEntries = new VBox(8);
    private final TextField nameField = new TextField();
    /** Playlists whose songs are currently shown (all are collapsed at first). */
    private final Set<Playlist> expanded = new HashSet<>();

    private Playlist selected;

    public PlaylistScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        outer.setStyle(Theme.BACKGROUND_STYLE);

        VBox middle = createMiddleColumn();
        VBox.setVgrow(middle, Priority.ALWAYS);
        // Everything right of the left column, with the now playing bar at the bottom
        VBox content = new VBox(middle, new NowPlayingBar());
        HBox.setHgrow(content, Priority.ALWAYS);

        outer.getChildren().addAll(createLeftColumn(), content);
        refresh();
    }

    /** Rebuilds the playlist list, e.g. after changes made in another scene. */
    public void refresh() {
        playlistEntries.getChildren().clear();
        for (Playlist playlist : playlists()) {
            playlistEntries.getChildren().add(createPlaylistBlock(playlist));
        }
    }

    // ---------- Left column: actions ----------

    private VBox createLeftColumn() {
        VBox left = new VBox(10);
        left.setPrefWidth(Theme.SIDEBAR_WIDTH);
        left.setMinWidth(220);
        left.setPadding(new Insets(20));
        left.setStyle(Theme.PANEL_STYLE + Theme.DIVIDER_STYLE + "-fx-border-width: 0 1px 0 0;");

        nameField.setPromptText("Playlist name");
        nameField.setStyle(Theme.INPUT_STYLE);
        nameField.setOnAction(e -> createPlaylist());

        Button createButton = UI.flatButton("Create Playlist", Icon.ADD);
        createButton.setOnAction(e -> createPlaylist());

        Button backButton = UI.flatButton("Go back", Icon.BACK);
        backButton.setOnAction(e -> sceneManager.showMainMenu());

        left.getChildren().addAll(UI.header("Playlists"), nameField, createButton, UI.vSpacer(), backButton);
        return left;
    }

    // ---------- Middle column: playlist list ----------

    private VBox createMiddleColumn() {
        VBox middle = new VBox(10);
        middle.setPadding(new Insets(20));

        playlistEntries.setPadding(new Insets(0, 4, 0, 0));
        // Clicking empty space in the list unselects the current playlist
        playlistEntries.setOnMouseClicked(e -> select(null));

        ScrollPane scrollPane = new ScrollPane(playlistEntries);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle(Theme.TRANSPARENT_SCROLL_STYLE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        middle.getChildren().addAll(UI.header("Your Playlists"), scrollPane);
        return middle;
    }

    /** A playlist row as header, followed by its songs when expanded. */
    private VBox createPlaylistBlock(Playlist playlist) {
        VBox block = new VBox(4);
        block.getChildren().add(createPlaylistRow(playlist));
        if (expanded.contains(playlist)) {
            block.getChildren().add(createTrackList(playlist));
        }
        return block;
    }

    private HBox createPlaylistRow(Playlist playlist) {
        CoverView cover = new CoverView(PLAYLIST_COVER_SIZE);
        cover.setImage(playlist.getCover());
        cover.setOnPlay(() -> play(playlist, 0));

        Label name = UI.label(playlist.getName(), 18, Theme.TEXT);
        // Underline on hover to show the name itself is clickable
        name.setOnMouseEntered(e -> name.setUnderline(true));
        name.setOnMouseExited(e -> name.setUnderline(false));
        // Clicking the name opens the playlist
        name.setOnMouseClicked(e -> {
            e.consume();
            selected = playlist;
            sceneManager.showPlaylistDetail(playlist);
        });

        Label count = UI.label(Formats.songCount(playlist.getTracks().size()), 13, Theme.TEXT_FAINT);
        VBox texts = new VBox(2, name, count);
        texts.setAlignment(Pos.CENTER_LEFT);
        texts.setMinWidth(0);

        Button expandButton = UI.iconButton(expanded.contains(playlist) ? Icon.TRIANGLE_DOWN : Icon.TRIANGLE_UP,
                7, Theme.MARKER_COLOR);
        expandButton.setOnAction(e -> toggleExpanded(playlist));

        HBox row = new HBox(14, cover, texts, UI.hSpacer(), expandButton, createOptionsMarker(texts, name, playlist));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 8, 8, 8));
        row.setMinHeight(PLAYLIST_COVER_SIZE + 16);
        row.setStyle(playlist == selected ? Theme.ROW_SELECTED_STYLE : Theme.ROW_STYLE);

        // Clicking anywhere else on the row selects it, clicking it again unselects it
        row.setOnMouseClicked(e -> {
            e.consume();
            select(playlist == selected ? null : playlist);
        });
        return row;
    }

    private Label createOptionsMarker(VBox texts, Label name, Playlist playlist) {
        return UI.optionsMarker(
                UI.menuItem("Add songs", () -> addSongs(playlist)),
                UI.menuItem("Change cover", () -> changeCover(playlist)),
                UI.menuItem("Rename", () -> UI.startInlineEdit(texts, name, playlist.getName(), playlist::setName, this::refresh)),
                UI.menuItem("Delete", () -> deletePlaylist(playlist))
        );
    }

    // ---------- Song rows ----------

    private VBox createTrackList(Playlist playlist) {
        VBox list = new VBox(4);
        list.setPadding(new Insets(0, 0, 4, TRACK_INDENT));

        List<AudioTrack> tracks = playlist.getTracks();
        if (tracks.isEmpty()) {
            Label empty = UI.label("No songs yet. Add some with ⋮ → Add songs.", 14, Theme.TEXT_FAINT);
            empty.setPadding(new Insets(10, 12, 10, 12));
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setStyle(Theme.TRACK_ROW_STYLE);
            list.getChildren().add(empty);
        }
        for (int i = 0; i < tracks.size(); i++) {
            list.getChildren().add(createTrackRow(playlist, i));
        }
        return list;
    }

    private HBox createTrackRow(Playlist playlist, int index) {
        AudioTrack track = playlist.getTracks().get(index);

        CoverView cover = new CoverView(TRACK_COVER_SIZE).withPlaceholderIcon(Icon.MUSIC_NOTE);
        cover.setImage(track.getCover());
        cover.setOnPlay(() -> play(playlist, index));

        VBox texts = new VBox(1,
                UI.label(track.getTitle(), 15, Theme.TEXT),
                UI.label(track.getArtist(), 13, Theme.TEXT_FAINT));
        texts.setMinWidth(0);

        HBox row = new HBox(12, cover, texts);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 12, 6, 8));
        row.setStyle(Theme.TRACK_ROW_STYLE);
        // Double click plays the song
        row.setOnMouseClicked(e -> {
            e.consume();
            if (e.getClickCount() == 2) play(playlist, index);
        });
        return row;
    }

    // ---------- Actions ----------

    private void createPlaylist() {
        List<Playlist> playlists = playlists();
        // Highest id + 1, so ids stay unique even after deleting playlists
        int id = playlists.stream().mapToInt(Playlist::getId).max().orElse(0) + 1;
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Playlist " + id;
        }
        Playlist playlist = new Playlist(name, id);
        playlists.add(playlist);
        nameField.clear();
        select(playlist);
    }

    private void select(Playlist playlist) {
        selected = playlist;
        refresh();
    }

    private void toggleExpanded(Playlist playlist) {
        if (!expanded.remove(playlist)) {
            expanded.add(playlist);
        }
        refresh();
    }

    private void deletePlaylist(Playlist playlist) {
        playlists().remove(playlist);
        expanded.remove(playlist);
        select(selected == playlist ? null : selected);
    }

    private void addSongs(Playlist playlist) {
        // Show the new songs right away
        expanded.add(playlist);
        AddTracksDialog.show(window(), playlist, this::refresh);
    }

    private void changeCover(Playlist playlist) {
        Image image = FileDialogs.chooseImage(window());
        if (image != null) {
            playlist.setCover(image);
            refresh();
        }
    }

    private void play(Playlist playlist, int index) {
        Client.getClient().getAudioPlayer().play(playlist.getTracks(), index);
    }

    // ---------- Helpers ----------

    private List<Playlist> playlists() {
        return Client.getClient().getPlaylistManager().getPlayaDenBossa();
    }

    private Window window() {
        return outer.getScene().getWindow();
    }

    public HBox getOuter() {
        return outer;
    }
}
