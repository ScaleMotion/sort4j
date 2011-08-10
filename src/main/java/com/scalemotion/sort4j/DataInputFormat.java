package com.scalemotion.sort4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Defines the way for reading (deserializing) objects from byte stream
 * @param <T> objects type
 */
public interface DataInputFormat<T>{
    /**
     * @param in input stream
     * @return reader
     */
    public Reader<T> initialize(InputStream in);

    /**
     * Reader interface
     * @param <T> object type
     */
    public static interface Reader<T> extends Closeable {
        public boolean hasNext() throws IOException;
        public T nextItem() throws IOException;
    }
}
