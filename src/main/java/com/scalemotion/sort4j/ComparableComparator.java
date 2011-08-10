package com.scalemotion.sort4j;

import java.util.Comparator;

/**
 * Comparator that compares "comparable" objects
 * @param <T> comparator
 */
public class ComparableComparator<T extends Comparable<T>>implements Comparator<T>{
    /**
     * {@inheritDoc}
     */
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }
}
