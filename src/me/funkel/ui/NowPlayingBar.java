package me.funkel.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import me.funkel.Client;
import me.funkel.media.AudioPlayer;
import me.funkel.media.AudioTrack;

/**
 * Bottom bar showing the last played song with play/pause and a playback slider.
 * Hidden until something was played. Every scene can create its own bar; all bars
 * show the state of the one shared {@link AudioPlayer}.
 */
public class NowPlayingBar extends VBox {

    private static final double COVER_SIZE = 56;
    private static final double PLAY_ICON_SIZE = 22;
    private static final double SKIP_ICON_SIZE = 16;

    private final AudioPlayer player = Client.getClient().getAudioPlayer();

    private final CoverView cover = new CoverView(COVER_SIZE).withPlaceholderIcon(Icon.MUSIC_NOTE);
    private final Label title = UI.boldLabel("", 17);
    private final Label artist = UI.label("", 14, Theme.TEXT_FAINT);
    private final Label time = UI.label("", 13, Theme.TEXT_FAINT);
    private final Button playButton = UI.iconButton(Icon.PLAY, PLAY_ICON_SIZE, Theme.TEXT);
    private final PlaybackSlider slider = new PlaybackSlider();

    // Kept as fields and registered weakly, so bars of closed scenes can be garbage collected
    private final ChangeListener<AudioTrack> trackListener = (obs, old, track) -> showTrack(track);
    private final ChangeListener<Boolean> playingListener = (obs, old, playing) -> updatePlayButton();
    private final ChangeListener<Duration> timeListener = (obs, old, value) -> updateTime();

    public NowPlayingBar() {
        setStyle(Theme.PANEL_STYLE + Theme.DIVIDER_STYLE + "-fx-border-width: 1px 0 0 0;");

        VBox texts = new VBox(2, title, artist);
        texts.setAlignment(Pos.CENTER_LEFT);
        texts.setMinWidth(0);

        Button previous = UI.iconButton(Icon.PREVIOUS, SKIP_ICON_SIZE, Theme.TEXT_SECONDARY);
        previous.setOnAction(e -> player.previous());
        playButton.setOnAction(e -> player.togglePlay());
        Button next = UI.iconButton(Icon.NEXT, SKIP_ICON_SIZE, Theme.TEXT_SECONDARY);
        next.setOnAction(e -> player.next());
        HBox controls = new HBox(4, previous, playButton, next);
        controls.setAlignment(Pos.CENTER);

        time.setMinWidth(USE_PREF_SIZE);

        HBox content = new HBox(14, cover, texts, UI.hSpacer(), controls, time);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(10, 20, 4, 20));

        slider.setOnSeek(this::seekTo);

        getChildren().addAll(content, slider);

        player.currentTrackProperty().addListener(new WeakChangeListener<>(trackListener));
        player.playingProperty().addListener(new WeakChangeListener<>(playingListener));
        player.currentTimeProperty().addListener(new WeakChangeListener<>(timeListener));
        player.totalTimeProperty().addListener(new WeakChangeListener<>(timeListener));

        showTrack(player.getCurrentTrack());
        updatePlayButton();
        updateTime();
    }

    private void showTrack(AudioTrack track) {
        setVisible(track != null);
        setManaged(track != null);
        if (track == null) return;
        cover.setImage(track.getCover());
        title.setText(track.getTitle());
        artist.setText(track.getArtist());
    }

    private void updatePlayButton() {
        UI.setIcon(playButton, player.isPlaying() ? Icon.PAUSE : Icon.PLAY, PLAY_ICON_SIZE, Theme.TEXT);
    }

    private void updateTime() {
        Duration current = player.currentTimeProperty().get();
        Duration total = player.totalTimeProperty().get();
        time.setText(Formats.duration(current) + " / " + Formats.duration(total));
        boolean known = Formats.isKnown(total) && total.toMillis() > 0;
        slider.setProgress(known ? current.toMillis() / total.toMillis() : 0);
    }

    private void seekTo(double fraction) {
        Duration total = player.totalTimeProperty().get();
        if (Formats.isKnown(total)) {
            player.seek(total.multiply(fraction));
        }
    }
}
