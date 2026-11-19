package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.funkel.playlists.Playlist;

public class PlaylistDetailScene {

    private final VBox outer = new VBox(10);

    public PlaylistDetailScene(SceneManager sceneManager, Playlist playlist) {
        outer.setPadding(new Insets(20));
        outer.setStyle("-fx-background-color: #181818;");

        Label title = new Label(playlist.getName());
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Label placeholder = new Label("This playlist is empty.");
        placeholder.setTextFill(Color.rgb(255, 255, 255, 0.6));
        placeholder.setFont(Font.font("Segoe UI", 15));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        String base = "-fx-font-size: 15px; -fx-font-family: 'Segoe UI'; -fx-text-fill: white;" +
                "-fx-background-radius: 6px; -fx-background-insets: 0; -fx-cursor: hand;";
        String normal = base + "-fx-background-color: rgba(255, 255, 255, 0.06);";
        String hover = base + "-fx-background-color: rgba(255, 255, 255, 0.14);";

        Button backButton = new Button("Go back");
        backButton.setPrefSize(200, 40);
        backButton.setFocusTraversable(false);
        backButton.setStyle(normal);
        backButton.setOnMouseEntered(e -> backButton.setStyle(hover));
        backButton.setOnMouseExited(e -> backButton.setStyle(normal));
        backButton.setOnAction(e -> sceneManager.showPlaylist());

        outer.getChildren().addAll(title, placeholder, spacer, backButton);
    }

    public VBox getOuter() {
        return outer;
    }
}
