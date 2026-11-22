package me.funkel.ui;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Locale;

/** System file choosers for audio files and cover images. */
public final class FileDialogs {

    /** Formats JavaFX can play. */
    public static final List<String> AUDIO_EXTENSIONS = List.of("mp3", "wav", "aif", "aiff", "m4a", "aac");
    public static final List<String> IMAGE_EXTENSIONS = List.of("png", "jpg", "jpeg", "gif", "bmp");

    /** Remembers the last folder, so the next chooser opens there again. */
    private static File lastDirectory;

    private FileDialogs() {
    }

    public static List<File> chooseAudioFiles(Window owner) {
        FileChooser chooser = createChooser("Choose audio files", "Audio files", AUDIO_EXTENSIONS);
        List<File> files = chooser.showOpenMultipleDialog(owner);
        if (files == null) return List.of();
        rememberDirectory(files.getFirst());
        return files;
    }

    /** Returns the chosen image or {@code null} if nothing (or an unreadable file) was chosen. */
    public static Image chooseImage(Window owner) {
        FileChooser chooser = createChooser("Choose cover image", "Images", IMAGE_EXTENSIONS);
        File file = chooser.showOpenDialog(owner);
        if (file == null) return null;
        rememberDirectory(file);
        Image image = new Image(file.toURI().toString());
        return image.isError() ? null : image;
    }

    public static boolean isAudioFile(File file) {
        String name = file.getName().toLowerCase(Locale.ROOT);
        return file.isFile() && AUDIO_EXTENSIONS.stream().anyMatch(ext -> name.endsWith("." + ext));
    }

    private static FileChooser createChooser(String title, String filterName, List<String> extensions) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterName,
                extensions.stream().map(ext -> "*." + ext).toList()));
        if (lastDirectory != null && lastDirectory.isDirectory()) {
            chooser.setInitialDirectory(lastDirectory);
        }
        return chooser;
    }

    private static void rememberDirectory(File file) {
        lastDirectory = file.getParentFile();
    }
}
