package sort4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Defines the way for writing (serializing) objects to output stream
 * @param <T> object type
 */
public interface DataOutputFormat<T>  {
    public Writer<T> initialize(OutputStream output);

    /**
     * Writer interface
     * @param <T> object type
     */
    public static interface Writer<T> extends Closeable {
        public void write(T item) throws IOException;
    }
}
