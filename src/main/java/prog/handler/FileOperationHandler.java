package prog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

/**
 * Handles file selection and file-related operations.
 */
public class FileOperationHandler {
    private static final Logger logger = LoggerFactory.getLogger(FileOperationHandler.class);

    private File selectedFile;
    private File outputFile;

    /**
     * Opens a file chooser dialog and allows the user to select a file.
     *
     * @return The selected file, or null if no file was selected
     */
    public File browseForFile() {
        logger.debug("Opening file chooser dialog");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select a file to compress or decompress");

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            logger.info("File selected: {} (size: {} bytes)", selectedFile.getAbsolutePath(), selectedFile.length());
            return selectedFile;
        } else {
            logger.debug("File selection cancelled");
            return null;
        }
    }

    /**
     * Validates that a file has been selected.
     *
     * @return true if a file is selected, false otherwise
     */
    public boolean isFileSelected() {
        if (selectedFile == null) {
            logger.warn("Operation attempted without file selection");
            return false;
        }
        return true;
    }

    /**
     * Formats a file size in human-readable format (B, KB, MB, GB).
     *
     * @param bytes The file size in bytes
     * @return Formatted string with appropriate unit
     */
    public String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        else if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        else return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    /**
     * Shows a warning dialog when no file is selected.
     */
    public void showNoFileSelectedWarning() {
        JOptionPane.showMessageDialog(null,
                "Please select a file first using the Browse button.",
                "No File Selected",
                JOptionPane.WARNING_MESSAGE);
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File file) {
        this.outputFile = file;
    }
}
