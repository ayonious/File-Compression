package prog.util;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Wrapper for writing bytes sequentially to a file.
 * Provides cleaner output stream operations.
 */
public class ByteWriter implements AutoCloseable {
    private final DataOutputStream dataOutputStream;
    private final FileOutputStream fileOutputStream;

    public ByteWriter(String filePath) throws IOException {
        this.fileOutputStream = new FileOutputStream(filePath);
        this.dataOutputStream = new DataOutputStream(fileOutputStream);
    }

    /**
     * Writes a single byte to the stream.
     */
    public void writeByte(int byteValue) throws IOException {
        dataOutputStream.write(byteValue);
    }

    /**
     * Writes an integer to the stream.
     */
    public void writeInt(int value) throws IOException {
        dataOutputStream.writeInt(value);
    }

    /**
     * Closes the underlying streams.
     */
    @Override
    public void close() throws IOException {
        if (dataOutputStream != null) {
            dataOutputStream.close();
        }
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
    }
}
