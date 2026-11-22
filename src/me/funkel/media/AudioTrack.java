package me.funkel.media;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * One local audio file inside a playlist. Title, artist etc. start with fallbacks
 * (file name, "Unknown artist") and are replaced by the file's tags once
 * {@link #loadMetadata} has finished.
 */
public class AudioTrack {

    private static int nextId = 1;

    private final int id;
    private final File file;
    private final LocalDateTime addedAt = LocalDateTime.now();

    private String title;
    private String artist = "Unknown artist";
    private String album = "";
    private String genre = "";
    private Duration length = Duration.UNKNOWN;
    private Image cover;

    public AudioTrack(File file) {
        this.id = nextId++;
        this.file = file;
        this.title = stripExtension(file.getName());
    }

    public Media createMedia() {
        return new Media(file.toURI().toString());
    }

    // ---------- Metadata ----------

    /** Reads tags and length of the file, then runs {@code onDone} on the FX thread (also on errors). */
    public void loadMetadata(Runnable onDone) {
        try {
            Media media = createMedia();
            MediaPlayer probe = new MediaPlayer(media);
            probe.setOnReady(() -> {
                readMetadata(media.getMetadata());
                length = media.getDuration();
                probe.dispose();
                onDone.run();
            });
            probe.setOnError(() -> {
                System.err.println("Could not read " + file + ": " + probe.getError());
                probe.dispose();
                onDone.run();
            });
        } catch (MediaException e) {
            System.err.println("Unsupported audio file " + file + ": " + e.getMessage());
            onDone.run();
        }
    }

    /** Loads the metadata of all tracks one after another, running {@code onEachLoaded} after every track. */
    public static void loadMetadata(List<AudioTrack> tracks, Runnable onEachLoaded) {
        if (tracks.isEmpty()) return;
        tracks.getFirst().loadMetadata(() -> {
            onEachLoaded.run();
            loadMetadata(tracks.subList(1, tracks.size()), onEachLoaded);
        });
    }

    private void readMetadata(Map<String, Object> metadata) {
        title = textOr(metadata, "title", title);
        artist = textOr(metadata, "artist", textOr(metadata, "album artist", artist));
        album = textOr(metadata, "album", album);
        genre = textOr(metadata, "genre", genre);
        if (metadata.get("image") instanceof Image image) {
            cover = image;
        }
    }

    private static String textOr(Map<String, Object> metadata, String key, String fallback) {
        return metadata.get(key) instanceof String text && !text.isBlank() ? text.trim() : fallback;
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }

    // ---------- Getters ----------

    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public Duration getLength() {
        return length;
    }

    public Image getCover() {
        return cover;
    }

    public long getFileSize() {
        return file.length();
    }

    /** File type in upper case, e.g. "MP3". */
    public String getFormat() {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1).toUpperCase(Locale.ROOT) : "?";
    }
}
