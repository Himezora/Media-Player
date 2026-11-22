package me.funkel.ui;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * Square cover art with rounded corners. Shows a blank tile while no image is set.
 * Optionally shows a darkened overlay with a play icon on hover (see {@link #setOnPlay}).
 */
public class CoverView extends StackPane {

    private final ImageView imageView = new ImageView();
    private final double size;
    private Region placeholderIcon;

    public CoverView(double size) {
        this.size = size;
        setMinSize(size, size);
        setPrefSize(size, size);
        setMaxSize(size, size);
        setBackground(new Background(new BackgroundFill(Theme.COVER_BLANK, new CornerRadii(Theme.CORNER_RADIUS), null)));

        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setSmooth(true);

        Rectangle clip = new Rectangle(size, size);
        clip.setArcWidth(Theme.CORNER_RADIUS * 2);
        clip.setArcHeight(Theme.CORNER_RADIUS * 2);
        setClip(clip);

        getChildren().add(imageView);
    }

    /** Shows a faint icon on the blank tile (e.g. a music note for songs without cover). */
    public CoverView withPlaceholderIcon(Icon icon) {
        placeholderIcon = icon.create(size * 0.4, Theme.TEXT_FAINT);
        placeholderIcon.setVisible(imageView.getImage() == null);
        getChildren().addFirst(placeholderIcon);
        return this;
    }

    /** Sets the cover, {@code null} shows the blank tile. Non-square images are cropped to their center. */
    public void setImage(Image image) {
        imageView.setImage(image);
        imageView.setViewport(image == null ? null : centerSquare(image));
        if (placeholderIcon != null) {
            placeholderIcon.setVisible(image == null);
        }
    }

    /** Enables the hover overlay; clicking it runs {@code action}. */
    public void setOnPlay(Runnable action) {
        StackPane overlay = new StackPane(Icon.PLAY.create(size * 0.4, Theme.TEXT));
        overlay.setBackground(Background.fill(Theme.COVER_OVERLAY));
        overlay.setStyle("-fx-cursor: hand;");
        overlay.setVisible(false);
        overlay.setOnMouseClicked(e -> {
            e.consume();
            action.run();
        });
        hoverProperty().addListener((obs, wasHovered, hovered) -> overlay.setVisible(hovered));
        getChildren().add(overlay);
    }

    private static Rectangle2D centerSquare(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        double side = Math.min(width, height);
        return new Rectangle2D((width - side) / 2, (height - side) / 2, side, side);
    }
}
