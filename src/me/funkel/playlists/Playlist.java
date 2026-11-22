package me.funkel.playlists;

import javafx.scene.image.Image;
import javafx.util.Duration;
import me.funkel.media.AudioTrack;
import me.funkel.ui.Formats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String name;
    private final int id;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final List<AudioTrack> tracks = new ArrayList<>();
    /** {@code null} means no cover was chosen yet (shown as a blank tile). */
    private Image cover;

    public Playlist(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<AudioTrack> getTracks() {
        return tracks;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    /** Sum of all known track lengths. */
    public Duration getTotalLength() {
        Duration total = Duration.ZERO;
        for (AudioTrack track : tracks) {
            if (Formats.isKnown(track.getLength())) {
                total = total.add(track.getLength());
            }
        }
        return total;
    }
}
