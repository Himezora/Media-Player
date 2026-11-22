package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import me.funkel.Client;
import me.funkel.media.AudioPlayer;
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

import java.util.List;

public class PlaylistDetailScene {

    private static final double PLAYLIST_COVER_SIZE = Theme.SIDEBAR_WIDTH - 40;
    private static final double SONG_COVER_SIZE = 260;
    private static final double ROW_COVER_SIZE = 52;
    /** Width after which the details on the right of a song row wrap into a new line. */
    private static final double DETAILS_WRAP_WIDTH = 330;

    private final SceneManager sceneManager;
    private final Playlist playlist;
    private final AudioPlayer player = Client.getClient().getAudioPlayer();

    private final HBox outer = new HBox();
    private final VBox left = new VBox(10);
    private final CoverView playlistCover = new CoverView(PLAYLIST_COVER_SIZE);
    private final Label playlistName = UI.boldLabel("", 26);
    private final Label playlistStats = UI.label("", 14, Theme.TEXT_FAINT);
    private final VBox trackEntries = new VBox(8);

    private final VBox right = new VBox(12);
    private final CoverView songCover = new CoverView(SONG_COVER_SIZE).withPlaceholderIcon(Icon.MUSIC_NOTE);
    private final Label songTitle = UI.boldLabel("", 22);
    private final Label songArtist = UI.label("", 16, Theme.TEXT_SECONDARY);
    private final Label songAlbum = UI.label("", 14, Theme.TEXT_FAINT);

    private AudioTrack selected;

    public PlaylistDetailScene(SceneManager sceneManager, Playlist playlist) {
        this.sceneManager = sceneManager;
        this.playlist = playlist;
        outer.setStyle(Theme.BACKGROUND_STYLE);

        VBox middle = createMiddleColumn();
        HBox.setHgrow(middle, Priority.ALWAYS);
        HBox columns = new HBox(middle, createRightColumn());
        VBox.setVgrow(columns, Priority.ALWAYS);
        // Everything right of the left column, with the now playing bar at the bottom
        VBox content = new VBox(columns, new NowPlayingBar());
        HBox.setHgrow(content, Priority.ALWAYS);

        outer.getChildren().addAll(createLeftColumn(), content);
        refresh();
    }

    /** Rebuilds everything that depends on the playlist's data. */
    private void refresh() {
        if (!playlist.getTracks().contains(selected)) {
            selected = null;
        }
        refreshLeftColumn();
        refreshTrackList();
        refreshRightColumn();
    }

    // ---------- Left column: playlist cover and actions ----------

    private VBox createLeftColumn() {
        left.setPrefWidth(Theme.SIDEBAR_WIDTH);
        left.setMinWidth(Theme.SIDEBAR_WIDTH);
        left.setPadding(new Insets(20));
        left.setStyle(Theme.PANEL_STYLE + Theme.DIVIDER_STYLE + "-fx-border-width: 0 1px 0 0;");

        playlistCover.setOnPlay(() -> play(0));
        playlistName.setWrapText(true);
        VBox.setMargin(playlistCover, new Insets(0, 0, 6, 0));
        VBox.setMargin(playlistStats, new Insets(0, 0, 10, 0));

        Button playButton = UI.flatButton("Play", Icon.PLAY);
        playButton.setOnAction(e -> play(0));
        Button shuffleButton = UI.flatButton("Shuffle", Icon.SHUFFLE);
        shuffleButton.setOnAction(e -> player.playShuffled(playlist.getTracks()));
        Button addButton = UI.flatButton("Add songs", Icon.ADD);
        addButton.setOnAction(e -> AddTracksDialog.show(window(), playlist, this::refresh));
        Button coverButton = UI.flatButton("Change cover", Icon.IMAGE);
        coverButton.setOnAction(e -> changeCover());
        Button renameButton = UI.flatButton("Rename", Icon.EDIT);
        renameButton.setOnAction(e -> UI.startInlineEdit(left, playlistName, playlist.getName(), playlist::setName, this::refresh));
        Button backButton = UI.flatButton("Go back", Icon.BACK);
        backButton.setOnAction(e -> sceneManager.showPlaylist());

        left.getChildren().addAll(playlistCover, playlistName, playlistStats,
                playButton, shuffleButton, addButton, coverButton, renameButton,
                UI.vSpacer(), backButton);
        return left;
    }

    private void refreshLeftColumn() {
        // Puts the name label back in case a rename text field replaced it
        left.getChildren().replaceAll(node -> node instanceof TextField ? playlistName : node);
        playlistCover.setImage(playlist.getCover());
        playlistName.setText(playlist.getName());
        playlistStats.setText(Formats.songCount(playlist.getTracks().size())
                + " · " + Formats.longDuration(playlist.getTotalLength()));
    }

    // ---------- Middle column: detailed song list ----------

    private VBox createMiddleColumn() {
        VBox middle = new VBox(10);
        middle.setPadding(new Insets(20));

        trackEntries.setPadding(new Insets(0, 4, 0, 0));
        // Clicking empty space in the list unselects the current song
        trackEntries.setOnMouseClicked(e -> select(null));

        ScrollPane scrollPane = new ScrollPane(trackEntries);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle(Theme.TRANSPARENT_SCROLL_STYLE);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        middle.getChildren().addAll(UI.header("Songs"), scrollPane);
        return middle;
    }

    private void refreshTrackList() {
        trackEntries.getChildren().clear();
        List<AudioTrack> tracks = playlist.getTracks();
        if (tracks.isEmpty()) {
            trackEntries.getChildren().add(UI.label("This playlist is empty. Add songs on the left.", 15, Theme.TEXT_FAINT));
        }
        for (int i = 0; i < tracks.size(); i++) {
            trackEntries.getChildren().add(createTrackRow(i));
        }
    }

    private HBox createTrackRow(int index) {
        AudioTrack track = playlist.getTracks().get(index);

        CoverView cover = new CoverView(ROW_COVER_SIZE).withPlaceholderIcon(Icon.MUSIC_NOTE);
        cover.setImage(track.getCover());
        cover.setOnPlay(() -> play(index));

        Label title = UI.label(track.getTitle(), 17, Theme.TEXT);
        Label artist = UI.label(track.getArtist(), 14, Theme.TEXT_FAINT);
        Label length = UI.label(Formats.duration(track.getLength()), 14, Theme.TEXT_FAINT);
        length.setMinWidth(Region.USE_PREF_SIZE);
        HBox subtitle = new HBox(12, artist, length);
        VBox texts = new VBox(2, title, subtitle);
        texts.setAlignment(Pos.CENTER_LEFT);
        texts.setMinWidth(0);

        Label marker = UI.optionsMarker(
                UI.menuItem("Play", () -> play(index)),
                UI.menuItem("Remove from playlist", () -> removeTrack(track))
        );

        HBox row = new HBox(14, cover, texts, UI.hSpacer(), createDetails(track, index), marker);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 8, 8, 8));
        row.setStyle(track == selected ? Theme.TRACK_ROW_SELECTED_STYLE : Theme.TRACK_ROW_STYLE);

        // Click selects (shows the cover on the right), double click plays
        row.setOnMouseClicked(e -> {
            e.consume();
            if (e.getClickCount() == 2) {
                play(index);
            } else {
                select(track == selected ? null : track);
            }
        });
        return row;
    }

    /** The small "caption / value" pairs on the right of a song row; wraps when wider than DETAILS_WRAP_WIDTH. */
    private FlowPane createDetails(AudioTrack track, int index) {
        FlowPane details = new FlowPane(22, 8);
        details.setAlignment(Pos.CENTER_RIGHT);
        details.setPrefWrapLength(DETAILS_WRAP_WIDTH);
        details.setMinWidth(Region.USE_PREF_SIZE);
        details.getChildren().addAll(
                createDetail("Entry", "#" + (index + 1)),
                createDetail("Added", Formats.date(track.getAddedAt())),
                createDetail("Size", Formats.fileSize(track.getFileSize())),
                createDetail("Album", orDash(track.getAlbum())),
                createDetail("Genre", orDash(track.getGenre())),
                createDetail("Format", track.getFormat()),
                createDetail("ID", String.valueOf(track.getId()))
        );
        return details;
    }

    private VBox createDetail(String caption, String value) {
        Label valueLabel = UI.label(value, 14, Theme.TEXT_SECONDARY);
        valueLabel.setMaxWidth(140);
        return new VBox(0, UI.label(caption.toUpperCase(), 10, Theme.TEXT_FAINT), valueLabel);
    }

    // ---------- Right column: cover of the selected song ----------

    private VBox createRightColumn() {
        right.setPrefWidth(SONG_COVER_SIZE + 40);
        right.setMinWidth(SONG_COVER_SIZE + 40);
        right.setPadding(new Insets(20));
        right.setStyle(Theme.PANEL_STYLE + Theme.DIVIDER_STYLE + "-fx-border-width: 0 0 0 1px;");

        for (Label label : new Label[]{songTitle, songArtist, songAlbum}) {
            label.setWrapText(true);
        }
        VBox.setMargin(songCover, new Insets(0, 0, 6, 0));

        right.getChildren().addAll(songCover, songTitle, songArtist, songAlbum);
        return right;
    }

    private void refreshRightColumn() {
        right.setVisible(selected != null);
        right.setManaged(selected != null);
        if (selected == null) return;
        songCover.setImage(selected.getCover());
        songTitle.setText(selected.getTitle());
        songArtist.setText(selected.getArtist());
        songAlbum.setText(selected.getAlbum());
    }

    // ---------- Actions ----------

    private void select(AudioTrack track) {
        selected = track;
        refresh();
    }

    private void play(int index) {
        player.play(playlist.getTracks(), index);
    }

    private void removeTrack(AudioTrack track) {
        playlist.getTracks().remove(track);
        refresh();
    }

    private void changeCover() {
        Image image = FileDialogs.chooseImage(window());
        if (image != null) {
            playlist.setCover(image);
            refresh();
        }
    }

    // ---------- Helpers ----------

    private static String orDash(String text) {
        return text.isBlank() ? "–" : text;
    }

    private Window window() {
        return outer.getScene().getWindow();
    }

    public HBox getOuter() {
        return outer;
    }
}
