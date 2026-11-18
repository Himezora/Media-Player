package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class AccountScene {

    private VBox outer = new VBox(10);

    public AccountScene(SceneManager sceneManager) {
        Button backButton = new Button("Go back");
        backButton.setPrefSize(200, 50);
        //backButton.setAlignment(Pos.BOTTOM_LEFT);
        //BorderPane.setAlignment(backButton, Pos.BOTTOM_LEFT);
        VBox.setMargin(backButton, new Insets(0, 0, 20, 20));
        outer.setAlignment(Pos.BOTTOM_LEFT);
        outer.getChildren().add(backButton);
        backButton.setOnAction(e -> {
            sceneManager.showMainMenu();
        });
    }

    public void showTest() {

    }

    public VBox getOuter() {
        return outer;
    }
}
