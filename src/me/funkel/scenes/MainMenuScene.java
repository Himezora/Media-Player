package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.funkel.Client;

public class MainMenuScene {

    private final AnchorPane outer = new AnchorPane();
    private final Label title = new Label(Client.getClient().getCompanyName());
    private final VBox buttons = new VBox(10);

    public MainMenuScene(SceneManager sceneManager) {
        VBox sideBar = new VBox(10);
        sideBar.setStyle(
                "-fx-background-color: rgba(30, 30, 30, 0.72);" +
                        "-fx-border-color: rgba(255, 255, 255, 0.55);" +
                        "-fx-border-width: 0 1px 0 0;"
        );
        double sideBarWidth = 250;
        sideBar.setPrefWidth(sideBarWidth);
        sideBar.setMaxWidth(sideBarWidth);
        sideBar.setAlignment(Pos.TOP_CENTER);
        sideBar.setPadding(new Insets(10, 0, 0, 0));
        AnchorPane.setTopAnchor(sideBar, 0.0);
        AnchorPane.setBottomAnchor(sideBar, 0.0);
        AnchorPane.setLeftAnchor(sideBar, 0.0);

        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Button buttonAccount = new Button("Account");
        Button buttonPlaylist = new Button("Playlist");
        Button buttonSettings = new Button("Settings");
        double buttonWidth = sideBarWidth * 0.8;
        String baseStyle =
                "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;" +
                        "-fx-cursor: hand;";
        String normalStyle = baseStyle + "-fx-background-color: transparent;";
        String hoverStyle = baseStyle + "-fx-background-color: rgba(255, 255, 255, 0.12);";
        String pressedStyle = baseStyle + "-fx-background-color: rgba(255, 255, 255, 0.22);";
        for (Button button : new Button[]{buttonAccount, buttonPlaylist, buttonSettings}) {
            button.setPrefWidth(buttonWidth);
            button.setPrefHeight(40);
            // Prevent the first button from being focused/highlighted on start
            button.setFocusTraversable(false);
            button.setStyle(normalStyle);
            button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
            button.setOnMouseExited(e -> button.setStyle(normalStyle));
            button.setOnMousePressed(e -> button.setStyle(pressedStyle));
            button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? hoverStyle : normalStyle));
        }
        //buttons.setAlignment(Pos.TOP_CENTER);
        //StackPane.setAlignment(buttons, Pos.TOP_CENTER);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(buttonAccount, buttonPlaylist, buttonSettings);
        buttonAccount.setOnAction(e -> {
            sceneManager.showAccount();
        });
        buttonPlaylist.setOnAction(e -> {
            sceneManager.showPlaylist();
        });
        buttonSettings.setOnAction(e -> {
            sceneManager.showSettings();
        });

        //sideBarComponents.getChildren().addAll(title, buttons);
        //sideBar.getChildren().addAll(sideBarComponents);
        sideBar.getChildren().addAll(title, buttons);

        String backgroundURL = "https://images.unsplash.com/photo-1501854140801-50d01698950b" +
                "?q=80&w=1950&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
        Image image = new Image(backgroundURL);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                backgroundSize);
        outer.setBackground(new Background(backgroundImage));

        outer.getChildren().addAll(sideBar);
    }

    public AnchorPane getOuter() {
        return outer;
    }
}
