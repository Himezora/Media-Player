package me.funkel.ui;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/** Factory methods for the small building blocks every scene uses. */
public final class UI {

    private UI() {
    }

    // ---------- Labels ----------

    public static Label label(String text, double size, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Theme.font(size));
        return label;
    }

    public static Label boldLabel(String text, double size) {
        Label label = new Label(text);
        label.setTextFill(Theme.TEXT);
        label.setFont(Theme.boldFont(size));
        return label;
    }

    public static Label header(String text) {
        Label label = boldLabel(text, 22);
        VBox.setMargin(label, new Insets(0, 0, 10, 0));
        return label;
    }

    // ---------- Layout ----------

    public static Region hSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static Region vSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    // ---------- Buttons ----------

    /** Full width sidebar button with an icon in front of the text. */
    public static Button flatButton(String text, Icon icon) {
        String base = "-fx-font-size: 15px;" +
                "-fx-font-family: '" + Theme.FONT_FAMILY + "';" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6px;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 0 14px 0 14px;" +
                "-fx-cursor: hand;";
        Button button = new Button(text);
        if (icon != null) {
            button.setGraphic(icon.create(16, Theme.TEXT));
            button.setGraphicTextGap(12);
        }
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        button.setFocusTraversable(false);
        applyStateStyles(button,
                base + "-fx-background-color: rgba(255, 255, 255, 0.06);",
                base + "-fx-background-color: rgba(255, 255, 255, 0.14);",
                base + "-fx-background-color: rgba(255, 255, 255, 0.24);");
        return button;
    }

    /** Round, background-less button that only shows an icon. */
    public static Button iconButton(Icon icon, double iconSize, Color color) {
        String base = "-fx-background-radius: 999px;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 8px;" +
                "-fx-cursor: hand;";
        Button button = new Button();
        button.setGraphic(icon.create(iconSize, color));
        button.setFocusTraversable(false);
        applyStateStyles(button,
                base + "-fx-background-color: transparent;",
                base + "-fx-background-color: rgba(255, 255, 255, 0.14);",
                base + "-fx-background-color: rgba(255, 255, 255, 0.24);");
        // Clicking the button must not also trigger the click handler of the row it sits in
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        return button;
    }

    public static void setIcon(Button button, Icon icon, double iconSize, Color color) {
        button.setGraphic(icon.create(iconSize, color));
    }

    private static void applyStateStyles(Button button, String normal, String hover, String pressed) {
        button.setStyle(normal);
        button.setOnMouseEntered(e -> button.setStyle(hover));
        button.setOnMouseExited(e -> button.setStyle(normal));
        button.setOnMousePressed(e -> button.setStyle(pressed));
        button.setOnMouseReleased(e -> button.setStyle(button.isHover() ? hover : normal));
    }

    // ---------- Options menu ----------

    public static MenuItem menuItem(String text, Runnable action) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(e -> action.run());
        return item;
    }

    /** The "⋮" marker at the right edge of a row that opens a menu with the given items. */
    public static Label optionsMarker(MenuItem... items) {
        Label marker = new Label("⋮");
        marker.setTextFill(Theme.MARKER_COLOR);
        marker.setFont(Theme.boldFont(20));
        marker.setPadding(new Insets(0, 8, 0, 8));
        marker.setStyle("-fx-cursor: hand;");
        marker.setOnMouseEntered(e -> marker.setTextFill(Theme.TEXT));
        marker.setOnMouseExited(e -> marker.setTextFill(Theme.MARKER_COLOR));

        ContextMenu menu = new ContextMenu(items);
        menu.setStyle(Theme.MENU_STYLE);

        marker.setOnMouseClicked(e -> {
            e.consume();
            menu.show(marker, Side.BOTTOM, 0, 0);
        });
        return marker;
    }

    // ---------- Inline editing ----------

    /**
     * Swaps {@code replaced} for a text field. Enter commits the (non-empty) text,
     * Escape or clicking away cancels. {@code onFinish} runs exactly once afterwards
     * and should rebuild the UI (which also removes the text field).
     */
    public static void startInlineEdit(Pane parent, Node replaced, String initialText,
                                       Consumer<String> onCommit, Runnable onFinish) {
        TextField field = new TextField(initialText);
        field.setStyle(Theme.INPUT_STYLE);
        field.setOnMouseClicked(Event::consume);

        boolean[] finished = {false};
        Runnable finish = () -> {
            if (finished[0]) return;
            finished[0] = true;
            onFinish.run();
        };

        field.setOnAction(e -> {
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                onCommit.accept(text);
            }
            finish.run();
        });
        field.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) finish.run();
        });
        field.focusedProperty().addListener((obs, wasFocused, focused) -> {
            if (!focused) finish.run();
        });

        parent.getChildren().set(parent.getChildren().indexOf(replaced), field);
        field.requestFocus();
        field.selectAll();
    }
}
