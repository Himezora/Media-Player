package me.funkel.ui;

import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Vector icons (Material Design paths, 24x24 grid).
 * Each icon stores its path and the real width/height of the drawn shape,
 * so it can be scaled without being distorted.
 */
public enum Icon {

    PLAY("M8 5v14l11-7z", 11, 14),
    PAUSE("M6 19h4V5H6v14zm8-14v14h4V5h-4z", 12, 14),
    PREVIOUS("M6 6h2v12H6zm3.5 6l8.5 6V6z", 12, 12),
    NEXT("M6 18l8.5-6L6 6v12zM16 6v12h2V6h-2z", 12, 12),
    SHUFFLE("M10.59 9.17L5.41 4 4 5.41l5.17 5.17 1.42-1.41zM14.5 4l2.04 2.04L4 18.59 5.41 20 17.96 7.46 20 9.5V4h-5.5z" +
            "m.33 9.41l-1.41 1.41 3.13 3.13L14.5 20H20v-5.5l-2.04 2.04-3.13-3.13z", 16, 16),
    EDIT("M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34" +
            "c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z", 18, 18),
    IMAGE("M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z", 18, 18),
    ADD("M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z", 14, 14),
    BACK("M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z", 16, 16),
    MUSIC_NOTE("M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z", 12, 18),
    TRIANGLE_UP("M7 14l5-5 5 5z", 10, 5),
    TRIANGLE_DOWN("M7 10l5 5 5-5z", 10, 5),
    CLOSE("M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z", 14, 14);

    private final String path;
    private final double width;
    private final double height;

    Icon(String path, double width, double height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    /** Creates the icon as a colored region with the given height (width follows the icon's aspect ratio). */
    public Region create(double size, Color color) {
        SVGPath shape = new SVGPath();
        shape.setContent(path);

        Region icon = new Region();
        icon.setShape(shape);
        double iconWidth = size * width / height;
        icon.setMinSize(iconWidth, size);
        icon.setPrefSize(iconWidth, size);
        icon.setMaxSize(iconWidth, size);
        // Clicks go to the button / row behind the icon
        icon.setMouseTransparent(true);
        setColor(icon, color);
        return icon;
    }

    public static void setColor(Region icon, Color color) {
        icon.setBackground(Background.fill(color));
    }
}
