package sort4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public interface DataInputFormat<T>{
    public Reader<T> initialize(InputStream in);

    public static interface Reader<T> extends Closeable {
        public boolean hasNext() throws IOException;
        public T nextItem() throws IOException;
    }
}
