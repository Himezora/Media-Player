package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import me.funkel.Client;
import me.funkel.playlists.Playlist;

public class PlaylistScene {

    private BorderPane outer = new BorderPane();
    private VBox leftBar = new VBox(10);
    private VBox playlistEntries = new VBox(10);

    public PlaylistScene(SceneManager sceneManager) {
        Button backButton = new Button("Go back");
        backButton.setPrefSize(200, 50);
        backButton.setMinHeight(Region.USE_PREF_SIZE);
        BorderPane.setMargin(backButton, new Insets(0, 0, 20, 20));

        backButton.setOnAction(e -> {
            sceneManager.showMainMenu();
        });

        int playlistBarButtonWidth = 200;
        ScrollPane playListBar = new ScrollPane(playlistEntries);
        playListBar.setStyle("-fx-background-color: rgba(30, 30, 30, 0.72); -fx-background: transparent;");
        playListBar.setFitToWidth(true);
        playListBar.setMaxWidth(playlistBarButtonWidth * 3);
        playListBar.setPrefWidth(playlistBarButtonWidth * 3);
        VBox.setVgrow(playListBar, Priority.ALWAYS);

        Button createPlaylist = new Button("Create Playlist");
        createPlaylist.setOnAction(e -> {
            Playlist playlist = new Playlist("Playlist ", Client.getClient().getPlaylistManager().getPlayaDenBossa().size() + 1);
            Client.getClient().getPlaylistManager().getPlayaDenBossa().add(playlist);
            Button newPl = new Button(playlist.getName() + " " + playlist.getId());
            newPl.setMaxWidth(playlistBarButtonWidth);
            newPl.setPrefSize(playlistBarButtonWidth, 50);
            VBox.setMargin(newPl, new Insets(0, 0, 20, 0));
            playlistEntries.getChildren().add(newPl);
        });

        leftBar.getChildren().addAll(createPlaylist);
        outer.setLeft(leftBar);
        outer.setBottom(backButton);
        outer.setCenter(playListBar);
    }

    public BorderPane getOuter() {
        return outer;
    }
}
