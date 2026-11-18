package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class SettingsScene {

    private final StackPane outer = new StackPane();

    public SettingsScene(SceneManager sceneManager) {
        Button backButton = new Button("Go back");
        backButton.setPrefSize(200, 50);
        StackPane.setMargin(backButton, new Insets(0, 0, 20, 20));
        outer.setAlignment(Pos.BOTTOM_LEFT);
        outer.getChildren().add(backButton);
        backButton.setOnAction(e -> {
            sceneManager.showMainMenu();
        });
    }

    public StackPane getOuter() {
        return outer;
    }
}
