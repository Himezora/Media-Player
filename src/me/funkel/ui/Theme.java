package me.funkel.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Central place for all colors, fonts and styles, so every scene looks the same
 * and the design can be changed in one spot.
 */
public final class Theme {

    private Theme() {
    }

    // ---------- Fonts ----------

    public static final String FONT_FAMILY = "Segoe UI";

    public static Font font(double size) {
        return Font.font(FONT_FAMILY, size);
    }

    public static Font boldFont(double size) {
        return Font.font(FONT_FAMILY, FontWeight.BOLD, size);
    }

    // ---------- Colors ----------

    public static final Color TEXT = Color.WHITE;
    public static final Color TEXT_SECONDARY = Color.rgb(255, 255, 255, 0.75);
    public static final Color TEXT_FAINT = Color.rgb(255, 255, 255, 0.55);
    public static final Color MARKER_COLOR = Color.rgb(255, 255, 255, 0.6);
    public static final Color COVER_BLANK = Color.rgb(255, 255, 255, 0.12);
    public static final Color COVER_OVERLAY = Color.rgb(0, 0, 0, 0.55);
    public static final Color SLIDER_TRACK = Color.rgb(255, 255, 255, 0.2);
    public static final Color SLIDER_FILL = Color.WHITE;

    // ---------- Sizes ----------

    public static final double SIDEBAR_WIDTH = 250;
    public static final double CORNER_RADIUS = 6;

    // ---------- Styles ----------

    public static final String BACKGROUND_STYLE = "-fx-background-color: #669cd1;";
    public static final String PANEL_STYLE = "-fx-background-color: rgba(30, 30, 30, 0.85);";
    public static final String DIALOG_STYLE = "-fx-background-color: rgb(30, 30, 30);";
    public static final String DIVIDER_STYLE = "-fx-border-color: rgba(255, 255, 255, 0.2);";
    public static final String TRANSPARENT_SCROLL_STYLE = "-fx-background-color: transparent; -fx-background: transparent;";

    // Playlist rows (headers)
    public static final String ROW_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 6px; -fx-cursor: hand;";
    public static final String ROW_SELECTED_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.22); -fx-background-radius: 6px; -fx-cursor: hand;";

    // Song rows, darker than playlist rows to tell them apart
    public static final String TRACK_ROW_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.18); -fx-background-radius: 6px; -fx-cursor: hand;";
    public static final String TRACK_ROW_SELECTED_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.38); -fx-background-radius: 6px; -fx-cursor: hand;";

    public static final String INPUT_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.08);" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: rgba(255, 255, 255, 0.45);" +
            "-fx-background-radius: 6px;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 8px;";

    public static final String MENU_STYLE = "-fx-background-color: rgb(40, 40, 40);" +
            "-fx-background-radius: 6px;" +
            "-fx-text-base-color: white;" +
            "-fx-focused-text-base-color: white;" +
            "-fx-selection-bar: rgba(255, 255, 255, 0.14);";

    public static final String DROP_ZONE_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.04);" +
            "-fx-background-radius: 6px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.3);" +
            "-fx-border-style: segments(8, 6);" +
            "-fx-border-radius: 6px;";
    public static final String DROP_ZONE_ACTIVE_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.12);" +
            "-fx-background-radius: 6px;" +
            "-fx-border-color: white;" +
            "-fx-border-style: segments(8, 6);" +
            "-fx-border-radius: 6px;";
}
