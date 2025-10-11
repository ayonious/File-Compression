package prog.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Wrapper for reading bytes sequentially from a file.
 * Provides cleaner iteration over byte streams.
 */
public class ByteReader implements AutoCloseable {
    private final DataInputStream dataInputStream;
    private final FileInputStream fileInputStream;

    public ByteReader(String filePath) throws IOException {
        this.fileInputStream = new FileInputStream(filePath);
        this.dataInputStream = new DataInputStream(fileInputStream);
    }

    /**
     * Reads the next byte and returns it.
     * Returns null if EOF is reached.
     */
    public Byte readNextByte() throws IOException {
        try {
            return dataInputStream.readByte();
        } catch (EOFException e) {
            return null;
        }
    }

    /**
     * Reads an integer from the stream.
     */
    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    /**
     * Closes the underlying streams.
     */
    @Override
    public void close() throws IOException {
        if (dataInputStream != null) {
            dataInputStream.close();
        }
        if (fileInputStream != null) {
            fileInputStream.close();
        }
    }
}
