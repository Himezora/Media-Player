package me.funkel.scenes;

import javafx.scene.Scene;

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
        scene.setRoot(playlistScene.getOuter());
    }

    public void showSettings() {
        scene.setRoot(settingsScene.getOuter());
    }

    public Scene getScene() {
        return scene;
    }
}
