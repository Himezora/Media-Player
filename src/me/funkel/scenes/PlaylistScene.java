package me.funkel.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import me.funkel.Client;
import me.funkel.playlists.Playlist;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PlaylistScene {

    private static final String PANEL_STYLE = "-fx-background-color: rgba(30, 30, 30, 0.85);";
    private static final String DIVIDER_STYLE = "-fx-border-color: rgba(255, 255, 255, 0.2);";
    private static final String ROW_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 6px; -fx-cursor: hand;";
    private static final String ROW_SELECTED_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.22); -fx-background-radius: 6px; -fx-cursor: hand;";
    private static final String INPUT_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.08);" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.45);" +
            "-fx-background-radius: 6px;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 8px;";
    private static final String MENU_STYLE = "-fx-background-color: rgb(40, 40, 40);" +
            "-fx-background-radius: 6px;" +
            "-fx-text-base-color: white;" +
            "-fx-focused-text-base-color: white;" +
            "-fx-selection-bar: rgba(255, 255, 255, 0.14);";
    private static final Color MARKER_COLOR = Color.rgb(255, 255, 255, 0.6);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final SceneManager sceneManager;
    private final HBox outer = new HBox();
    private final VBox playlistEntries = new VBox(8);
    private final VBox infoContent = new VBox(10);
    private final TextField nameField = new TextField();
    private final VBox right;

    private Playlist selected;

    public PlaylistScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        outer.setStyle("-fx-background-color: #669cd1;");

        VBox left = createLeftColumn();
        VBox middle = createMiddleColumn();
        right = createRightColumn();
        HBox.setHgrow(middle, Priority.ALWAYS);

        outer.getChildren().addAll(left, middle, right);
        refreshList();
        refreshInfo();
    }

    // ---------- Left column: actions ----------

    private VBox createLeftColumn() {
        VBox left = new VBox(10);
        left.setPrefWidth(250);
        left.setMinWidth(220);
        left.setPadding(new Insets(20));
        left.setStyle(PANEL_STYLE + DIVIDER_STYLE + "-fx-border-width: 0 1px 0 0;");

        Label header = createHeader("Playlists");

        nameField.setPromptText("Playlist name");
        nameField.setStyle(INPUT_STYLE);
        nameField.setOnAction(e -> createPlaylist());

        Button createButton = createFlatButton("Create Playlist");
        createButton.setOnAction(e -> createPlaylist());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button backButton = createFlatButton("Go back");
        backButton.setOnAction(e -> sceneManager.showMainMenu());

        left.getChildren().addAll(header, nameField, createButton, spacer, backButton);
        return left;
    }

    private void createPlaylist() {
        List<Playlist> playlists = Client.getClient().getPlaylistManager().getPlayaDenBossa();
        // Highest id + 1, so ids stay unique even after deleting playlists
        int id = playlists.stream().mapToInt(Playlist::getId).max().orElse(0) + 1;
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Playlist " + id;
        }
        Playlist playlist = new Playlist(name, id);
        playlists.add(playlist);
        nameField.clear();
        select(playlist);
    }

    private void select(Playlist playlist) {
        selected = playlist;
        refreshList();
        refreshInfo();
    }

    private void deletePlaylist(Playlist playlist) {
        Client.getClient().getPlaylistManager().getPlayaDenBossa().remove(playlist);
        select(selected == playlist ? null : selected);
    }

    private void toggleInfo() {
        right.setVisible(!right.isVisible());
        right.setManaged(right.isVisible());
    }

    // ---------- Middle column: playlist list ----------

    private VBox createMiddleColumn() {
        VBox middle = new VBox(10);
        middle.setPadding(new Insets(20));

        Label header = createHeader("Your Playlists");

        playlistEntries.setPadding(new Insets(0, 4, 0, 0));
        // Clicking empty space in the list unselects the current playlist
        playlistEntries.setOnMouseClicked(e -> select(null));

        ScrollPane scrollPane = new ScrollPane(playlistEntries);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        middle.getChildren().addAll(header, scrollPane);
        return middle;
    }

    private void refreshList() {
        playlistEntries.getChildren().clear();
        for (Playlist playlist : Client.getClient().getPlaylistManager().getPlayaDenBossa()) {
            playlistEntries.getChildren().add(createPlaylistRow(playlist));
        }
    }

    private HBox createPlaylistRow(Playlist playlist) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 8, 0, 14));
        row.setPrefHeight(50);
        row.setMinHeight(50);
        row.setStyle(playlist == selected ? ROW_SELECTED_STYLE : ROW_STYLE);

        Label name = new Label(playlist.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("Segoe UI", 16));
        // Underline on hover to show the name itself is clickable
        name.setOnMouseEntered(e -> name.setUnderline(true));
        name.setOnMouseExited(e -> name.setUnderline(false));

        // Clicking the name opens the playlist
        name.setOnMouseClicked(e -> {
            e.consume();
            selected = playlist;
            sceneManager.showPlaylistDetail(playlist);
        });

        // Clicking anywhere else on the row selects it, clicking it again unselects it
        row.setOnMouseClicked(e -> {
            e.consume();
            select(playlist == selected ? null : playlist);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(name, spacer, createOptionsMarker(row, name, playlist));
        return row;
    }

    // The "⋮" marker at the right edge of a row that opens the options menu
    private Label createOptionsMarker(HBox row, Label name, Playlist playlist) {
        Label marker = new Label("⋮");
        marker.setTextFill(MARKER_COLOR);
        marker.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        marker.setPadding(new Insets(0, 8, 0, 8));
        marker.setOnMouseEntered(e -> marker.setTextFill(Color.WHITE));
        marker.setOnMouseExited(e -> marker.setTextFill(MARKER_COLOR));

        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(e -> startRename(row, name, playlist));
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> deletePlaylist(playlist));
        MenuItem info = new MenuItem("Infos");
        info.setOnAction(e -> toggleInfo());

        ContextMenu menu = new ContextMenu(rename, delete, info);
        menu.setStyle(MENU_STYLE);

        marker.setOnMouseClicked(e -> {
            e.consume();
            //info.setText(right.isVisible() ? "Hide Info" : "Show Info");
            menu.show(marker, Side.BOTTOM, 0, 0);
        });
        return marker;
    }

    // Swaps the name label for a text field: Enter saves, Escape or clicking away cancels
    private void startRename(HBox row, Label name, Playlist playlist) {
        TextField field = new TextField(playlist.getName());
        field.setStyle(INPUT_STYLE);
        field.setOnMouseClicked(e -> e.consume());
        field.setOnAction(e -> {
            String newName = field.getText().trim();
            if (!newName.isEmpty()) {
                playlist.setName(newName);
            }
            refreshList();
            refreshInfo();
        });
        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) refreshList();
        });
        field.focusedProperty().addListener((obs, wasFocused, focused) -> {
            if (!focused) refreshList();
        });

        row.getChildren().set(row.getChildren().indexOf(name), field);
        field.requestFocus();
        field.selectAll();
    }

    // ---------- Right column: info ----------

    private VBox createRightColumn() {
        VBox right = new VBox(10);
        right.setPrefWidth(300);
        right.setMinWidth(240);
        right.setPadding(new Insets(20));
        right.setStyle(PANEL_STYLE + DIVIDER_STYLE + "-fx-border-width: 0 0 0 1px;");

        right.getChildren().addAll(createHeader("Info"), infoContent);
        return right;
    }

    private void refreshInfo() {
        infoContent.getChildren().clear();
        if (selected == null) {
            infoContent.getChildren().add(createInfoLabel("No playlist selected."));
            return;
        }
        Label name = new Label(selected.getName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        name.setWrapText(true);

        infoContent.getChildren().addAll(
                name,
                createInfoLabel("ID: " + selected.getId()),
                createInfoLabel("Created: " + selected.getCreatedAt().format(DATE_FORMAT))
        );
    }

    // ---------- Helpers ----------

    private Label createHeader(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        VBox.setMargin(label, new Insets(0, 0, 10, 0));
        return label;
    }

    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.rgb(255, 255, 255, 0.75));
        label.setFont(Font.font("Segoe UI", 14));
        label.setWrapText(true);
        return label;
    }

    private Button createFlatButton(String text) {
        String base = "-fx-font-size: 15px;" +
                "-fx-font-family: 'Segoe UI';" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-background-insets: 0;" +
                "-fx-cursor: hand;";
        String normal = base + "-fx-background-color: rgba(255, 255, 255, 0.06);";
        String hover = base + "-fx-background-color: rgba(255, 255, 255, 0.14);";
        String pressed = base + "-fx-background-color: rgba(255, 255, 255, 0.24);";

        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setFocusTraversable(false);
        button.setStyle(normal);
        button.setOnMouseEntered(e -> button.setStyle(hover));
        button.setOnMouseExited(e -> button.setStyle(normal));
        button.setOnMousePressed(e -> button.setStyle(pressed));
        button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? hover : normal));
        return button;
    }

    public HBox getOuter() {
        return outer;
    }
}
