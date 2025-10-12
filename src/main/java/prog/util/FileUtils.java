package prog.util;

/**
 * Utility class for file operations.
 * Provides file-related helper methods for the file compression application.
 */
public class FileUtils {

    /**
     * Checks if a file is empty by checking its length.
     *
     * Examples:
     * - Empty file (0 bytes) returns true
     * - File with 1 byte returns false
     * - File with any content returns false
     *
     * @param filePath Path to the file to check
     * @return true if file is empty (length = 0), false otherwise
     */
    public static boolean isEmptyFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        return file.length() == 0;
    }

    /**
     * Validates that an output file path is not null or empty.
     *
     * Examples:
     * - validateOutputPath(null) throws IllegalArgumentException
     * - validateOutputPath("") throws IllegalArgumentException
     * - validateOutputPath("  ") throws IllegalArgumentException
     * - validateOutputPath("/valid/path.txt") does nothing
     *
     * @param outputFilePath The output file path to validate
     * @throws IllegalArgumentException if outputFilePath is null or empty/whitespace
     */
    public static void validateOutputFilePath(String outputFilePath) {
        if (outputFilePath == null || outputFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Output file path cannot be null or empty");
        }
    }

    /**
     * Generates a unique file path if the given path already exists.
     * Appends a number in parentheses before the file extension to create a unique name.
     *
     * Examples:
     * - If "document.txt" exists, returns "document (1).txt"
     * - If "document (1).txt" also exists, returns "document (2).txt"
     * - If "file.tar.gz" exists, returns "file (1).tar.gz"
     * - If "noextension" exists, returns "noextension (1)"
     *
     * @param filePath The original file path
     * @return A unique file path (returns original if it doesn't exist)
     */
    public static String getUniqueFilePath(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            return filePath;
        }

        // Split the path into directory, filename, and extension
        java.io.File parentDir = file.getParentFile();
        String fileName = file.getName();
        String baseName;
        String extension;

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            baseName = fileName.substring(0, lastDotIndex);
            extension = fileName.substring(lastDotIndex);
        } else {
            baseName = fileName;
            extension = "";
        }

        // Try incrementing numbers until we find a unique filename
        int counter = 1;
        String newFilePath;
        do {
            String newFileName = baseName + " (" + counter + ")" + extension;
            if (parentDir != null) {
                newFilePath = new java.io.File(parentDir, newFileName).getAbsolutePath();
            } else {
                newFilePath = newFileName;
            }
            counter++;
        } while (new java.io.File(newFilePath).exists());

        return newFilePath;
    }
}
