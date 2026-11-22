package me.funkel.scenes;

import javafx.scene.Scene;
import me.funkel.playlists.Playlist;

public class SceneManager {

    private final Scene scene;

    private final MainMenuScene mainMenuScene = new MainMenuScene(this);
    private final AccountScene accountScene = new AccountScene(this);
    private final PlaylistScene playlistScene = new PlaylistScene(this);
    private final SettingsScene settingsScene = new SettingsScene(this);

    public SceneManager(Scene scene) {
        this.scene = scene;
    }

    public void showMainMenu() {
        scene.setRoot(mainMenuScene.getOuter());
    }

    public void showAccount() {
        scene.setRoot(accountScene.getOuter());
    }

    public void showPlaylist() {
        // Changes made in the detail scene (rename, cover, songs) must show up in the list
        playlistScene.refresh();
        scene.setRoot(playlistScene.getOuter());
    }

    public void showPlaylistDetail(Playlist playlist) {
        scene.setRoot(new PlaylistDetailScene(this, playlist).getOuter());
    }

    public void showSettings() {
        scene.setRoot(settingsScene.getOuter());
    }

    public Scene getScene() {
        return scene;
    }
}
