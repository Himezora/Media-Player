package me.funkel.ui;

import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

import java.util.function.DoubleConsumer;

/**
 * Thin progress line at the bottom edge of its area. Gets thicker on hover;
 * clicking or dragging seeks (reported as a fraction between 0 and 1 on release).
 */
public class PlaybackSlider extends Pane {

    private static final double HEIGHT = 12;
    private static final double LINE = 4;
    private static final double LINE_HOVER = 7;

    private final Region track = new Region();
    private final Region fill = new Region();
    private double progress;
    private boolean dragging;
    private DoubleConsumer onSeek = fraction -> {};

    public PlaybackSlider() {
        setMinHeight(HEIGHT);
        setPrefHeight(HEIGHT);
        setMaxHeight(HEIGHT);
        setStyle("-fx-cursor: hand;");
        track.setBackground(Background.fill(Theme.SLIDER_TRACK));
        fill.setBackground(Background.fill(Theme.SLIDER_FILL));
        getChildren().addAll(track, fill);

        setOnMousePressed(e -> {
            dragging = true;
            setProgressInternal(e.getX() / getWidth());
        });
        setOnMouseDragged(e -> setProgressInternal(e.getX() / getWidth()));
        setOnMouseReleased(e -> {
            dragging = false;
            onSeek.accept(progress);
            requestLayout();
        });
        hoverProperty().addListener((obs, was, hovered) -> requestLayout());
    }

    /** Updates the shown progress (ignored while the user is dragging). */
    public void setProgress(double fraction) {
        if (!dragging) {
            setProgressInternal(fraction);
        }
    }

    public void setOnSeek(DoubleConsumer onSeek) {
        this.onSeek = onSeek;
    }

    private void setProgressInternal(double fraction) {
        progress = Math.clamp(fraction, 0, 1);
        requestLayout();
    }

    @Override
    protected void layoutChildren() {
        double line = isHover() || dragging ? LINE_HOVER : LINE;
        double y = getHeight() - line;
        track.resizeRelocate(0, y, getWidth(), line);
        fill.resizeRelocate(0, y, getWidth() * progress, line);
    }
}
