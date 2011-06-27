package sort4j;

import sort4j.text.StringMemoryCalculator;
import sort4j.text.TextInputFormat;
import sort4j.text.TextOutputFormat;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class that describes data for sorting and it's format
 * @param <T> type of item in data
 */
public class SortingTask<T> {
    private static final String NOT_SET_ERROR_POSTFIX = "is not set for SortingTask";
    private DataInputFormat<T> inputFormat;
    private DataOutputFormat<T> outputFormat;
    private List<String> inputFiles;
    private String outputFile;
    private Comparator<T> comparator = new ComparableComparator();
    private MemoryCalculator<T> memoryCalculator;


    /**
     * Sets memory calculator: a callback which defines the usage of memory
     * of each item (see {@link MemoryCalculator}). If you're sorting text data, use
     * {@link sort4j.text.StringMemoryCalculator}
     * <p>
     * Should be called (no default value)
     * @param memoryCalculator calculator
     * @return this
     */
    public SortingTask<T> memoryCalculator(MemoryCalculator<T> memoryCalculator) {
        this.memoryCalculator = memoryCalculator;
        return this;
    }

    /**
     * Should be called (no default value)
     * @param outputFile output files
     * @return this
     */
    public SortingTask<T> outputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    /**
     * Format of input data (see {@link DataInputFormat}). If you're soring
     * text files use {@link sort4j.text.TextInputFormat}
     * <p>
     * Should be called (no default value)
     * @param inputFormat format
     * @return this
     */
    public SortingTask<T> inputFormat(DataInputFormat<T> inputFormat) {
        this.inputFormat = inputFormat;
        return this;
    }

    /**
     * Format of output data (see {@link DataOutputFormat}). If you're soring
     * text files use {@link sort4j.text.TextOutputFormat}
     * <p>
     * Should be called (no default value)
     * @param outputFormat format
     * @return this
     */
    public SortingTask<T> outputFormat(DataOutputFormat<T> outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    /**
     * @param inputFile single input file (if you need to sort multiple input files, use
     * {@link #inputFiles(java.util.List)})
     * @return this
     */
    public SortingTask<T> inputFile(String inputFile) {
        return inputFiles(Collections.singletonList(inputFile));
    }

    /**
     * @param inputFiles list of input files
     * @return this
     */
    public SortingTask<T> inputFiles(List<String> inputFiles) {
        this.inputFiles = inputFiles;
        return this;
    }

    /**
     * Sets the comparator. If you're sorting text data, use default value (simply don't call this method).
     * The default value is {@link ComparableComparator}
     * @param comparator comparator
     * @return this
     */
    public SortingTask<T> comparator(Comparator<T> comparator) {
        this.comparator = comparator;
        return this;
    }

    /**
     * @return getter for {@link #inputFormat(DataInputFormat)}
     */
    public DataInputFormat<T> inputFormat() {
        return inputFormat;
    }

    /**
     * @return getter for {@link #outputFormat(DataOutputFormat)}
     */
    public DataOutputFormat<T> outputFormat() {
        return outputFormat;
    }

    /**
     * @return getter for {@link #inputFiles(java.util.List)}
     */
    public List<String> inputFiles() {
        return inputFiles;
    }

    /**
     * @return getter for {@link #outputFile(String)}
     */
    public String outputFile() {
        return outputFile;
    }

    /**
     * @return getter for {@link #comparator(java.util.Comparator)}
     */
    public Comparator<T> comparator() {
        return comparator;
    }

    /**
     * @return getter for {@link #memoryCalculator(MemoryCalculator)}
     */
    public MemoryCalculator<T> memoryCalculator() {
        return memoryCalculator;
    }

    /**
     * Sorts task with given sorter
     * @param sorter sorter
     */
    public void sort(Sorter<T> sorter) {
        sorter.sort(this);
    }

    /**
     * Checks that all fields are set and have valid values
     */
    public void validate() {
        if (inputFormat == null) {
            throw new IllegalStateException("inputFormat " + NOT_SET_ERROR_POSTFIX);
        }
        if (outputFormat == null) {
            throw new IllegalStateException("outputFormat " + NOT_SET_ERROR_POSTFIX);
        }
        if (inputFiles == null) {
            throw new IllegalStateException("inputFiles " + NOT_SET_ERROR_POSTFIX);
        }
        if (outputFile == null) {
            throw new IllegalStateException("outputFile " + NOT_SET_ERROR_POSTFIX + " ");
        }
        if (memoryCalculator == null) {
            throw new IllegalStateException("memoryCalculator " + NOT_SET_ERROR_POSTFIX);
        }

    }

    /**
     * @return task template for sorting text files
     */
    public static SortingTask<String> createTextTask() {
        return new SortingTask<String>().inputFormat(new TextInputFormat()).outputFormat(new TextOutputFormat()).memoryCalculator(new StringMemoryCalculator());
    }
}
