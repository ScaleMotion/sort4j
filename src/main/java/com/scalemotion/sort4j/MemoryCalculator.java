package com.scalemotion.sort4j;

/**
 * Interface that defines the size of object in bytes
 * @param <T>
 */
public interface MemoryCalculator<T> {
    /**
     * @param item object
     * @return size in bytes
     */
    public int sizeof(T item);
}
