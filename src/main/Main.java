package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.funkel.Client;
import me.funkel.scenes.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Client.getClient().init();

        stage.setTitle(Client.getClient().getClientName());

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setWidth(screenWidth * 0.6);
        stage.setHeight(screenHeight * 0.7);

        Scene scene = new Scene(new Pane());
        SceneManager sceneManager = new SceneManager(scene);
        sceneManager.showMainMenu();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
