package me.funkel;

import me.funkel.media.AudioPlayer;
import me.funkel.playlists.PlaylistManager;

public class Client {

    private static final Client client = new Client();

    private String clientName, companyName;

    private final PlaylistManager playlistManager = new PlaylistManager();
    private final AudioPlayer audioPlayer = new AudioPlayer();

    public void init() {
        this.clientName = "Nocturne Media Player";
        this.companyName = "Nocturne Media";
    }

    public static Client getClient() {
        return client;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
