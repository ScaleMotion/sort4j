package sort4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface DataOutputFormat<T>  {
    public Writer<T> initialize(OutputStream output);

    public static interface Writer<T> extends Closeable {
        public void write(T item) throws IOException;
    }
}
