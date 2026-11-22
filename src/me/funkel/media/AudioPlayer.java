package me.funkel.media;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plays a queue of tracks, one at a time. The UI only talks to this class and
 * observes its properties (current track, playing state, time).
 */
public class AudioPlayer {

    /** Pressing "previous" later than this restarts the current song instead. */
    private static final Duration RESTART_THRESHOLD = Duration.seconds(3);

    private final ObjectProperty<AudioTrack> currentTrack = new SimpleObjectProperty<>();
    private final BooleanProperty playing = new SimpleBooleanProperty();
    private final ObjectProperty<Duration> currentTime = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<Duration> totalTime = new SimpleObjectProperty<>(Duration.UNKNOWN);

    private List<AudioTrack> queue = new ArrayList<>();
    private int index;
    private MediaPlayer mediaPlayer;

    // ---------- Controls ----------

    /** Plays {@code tracks} starting with the track at {@code startIndex}. */
    public void play(List<AudioTrack> tracks, int startIndex) {
        if (startIndex < 0 || startIndex >= tracks.size()) return;
        queue = new ArrayList<>(tracks);
        index = startIndex;
        playCurrent();
    }

    public void playShuffled(List<AudioTrack> tracks) {
        List<AudioTrack> shuffled = new ArrayList<>(tracks);
        Collections.shuffle(shuffled);
        play(shuffled, 0);
    }

    public void togglePlay() {
        if (mediaPlayer == null) return;
        if (playing.get()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    public void next() {
        if (index + 1 < queue.size()) {
            index++;
            playCurrent();
        }
    }

    public void previous() {
        if (index == 0 || currentTime.get().greaterThan(RESTART_THRESHOLD)) {
            seek(Duration.ZERO);
        } else {
            index--;
            playCurrent();
        }
    }

    public void seek(Duration time) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(time);
        }
    }

    // ---------- Internals ----------

    private void playCurrent() {
        disposePlayer();
        AudioTrack track = queue.get(index);
        currentTrack.set(track);
        currentTime.set(Duration.ZERO);
        totalTime.set(track.getLength());

        try {
            mediaPlayer = new MediaPlayer(track.createMedia());
        } catch (MediaException e) {
            System.err.println("Cannot play " + track.getFile() + ": " + e.getMessage());
            playing.set(false);
            return;
        }
        MediaPlayer player = mediaPlayer;
        player.currentTimeProperty().addListener((obs, old, time) -> currentTime.set(time));
        player.statusProperty().addListener((obs, old, status) -> playing.set(status == MediaPlayer.Status.PLAYING));
        player.setOnReady(() -> totalTime.set(player.getTotalDuration()));
        player.setOnEndOfMedia(this::onEndOfMedia);
        player.setOnError(() -> System.err.println("Playback error: " + player.getError()));
        player.play();
    }

    private void onEndOfMedia() {
        if (index + 1 < queue.size()) {
            next();
        } else {
            // End of the queue: stay on the last song, rewound and paused
            mediaPlayer.stop();
        }
    }

    private void disposePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // ---------- Properties ----------

    public ReadOnlyObjectProperty<AudioTrack> currentTrackProperty() {
        return currentTrack;
    }

    public AudioTrack getCurrentTrack() {
        return currentTrack.get();
    }

    public ReadOnlyBooleanProperty playingProperty() {
        return playing;
    }

    public boolean isPlaying() {
        return playing.get();
    }

    public ReadOnlyObjectProperty<Duration> currentTimeProperty() {
        return currentTime;
    }

    public ReadOnlyObjectProperty<Duration> totalTimeProperty() {
        return totalTime;
    }
}
