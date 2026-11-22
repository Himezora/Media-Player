package me.funkel.ui;

import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Turns raw values (durations, byte counts, dates) into display text. */
public final class Formats {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Formats() {
    }

    /** "3:07" or "1:02:45", "--:--" if unknown. */
    public static String duration(Duration duration) {
        if (!isKnown(duration)) {
            return "--:--";
        }
        long totalSeconds = (long) duration.toSeconds();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return hours > 0
                ? String.format("%d:%02d:%02d", hours, minutes, seconds)
                : String.format("%d:%02d", minutes, seconds);
    }

    /** "43 min" or "1 h 12 min", used for whole playlists. */
    public static String longDuration(Duration duration) {
        long totalMinutes = isKnown(duration) ? (long) duration.toMinutes() : 0;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours > 0 ? hours + " h " + minutes + " min" : minutes + " min";
    }

    public static String fileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    public static String date(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMAT);
    }

    public static String songCount(int count) {
        return count == 1 ? "1 song" : count + " songs";
    }

    public static boolean isKnown(Duration duration) {
        return duration != null && !duration.isUnknown() && !duration.isIndefinite();
    }
}
