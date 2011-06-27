package sort4j;

/**
 * Interface that represents sorting algorithm
 * @param <T>
 */
public interface Sorter<T> {
    /**
     * Sorts data defined by {@link SortingTask}
     * @param task task
     */
    public void sort(SortingTask<T> task);
}
