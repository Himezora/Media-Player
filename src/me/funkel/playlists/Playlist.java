package me.funkel.playlists;

import java.time.LocalDateTime;

public class Playlist {

    private String name;
    private final int id;
    private final LocalDateTime createdAt = LocalDateTime.now();

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
}
