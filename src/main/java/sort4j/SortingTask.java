package sort4j;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SortingTask<T> {
    private DataInputFormat<T> inputFormat;
    private DataOutputFormat<T> outputFormat;
    private List<File> inputFiles;
    private File temporaryDirectory;
    private File outputFile;
    private Comparator<T> comparator = new ComparableComparator();
    private MemoryCalculator<T> memoryCalculator;


    public SortingTask<T> memoryCalculator(MemoryCalculator<T> memoryCalculator) {
        this.memoryCalculator = memoryCalculator;
        return this;
    }

    public SortingTask<T> outputFile(File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public SortingTask<T> inputFormat(DataInputFormat<T> inputFormat) {
        this.inputFormat = inputFormat;
        return this;
    }
    public SortingTask<T> outputFormat(DataOutputFormat<T> outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public SortingTask<T> inputFiles(List<File> inputFiles) {
        this.inputFiles = inputFiles;
        return this;
    }

    public SortingTask<T> temporaryDirectory(File temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
        return this;
    }

    public SortingTask<T> comparator(Comparator<T> comparator) {
        this.comparator = comparator;
        return this;
    }

    public DataInputFormat<T> inputFormat() {
        return inputFormat;
    }

    public DataOutputFormat<T> outputFormat() {
        return outputFormat;
    }

    public List<File> inputFiles() {
        return inputFiles;
    }

    public File temporaryDirectory() {
        return temporaryDirectory;
    }

    public File outputFile() {
        return outputFile;
    }

    public Comparator<T> comparator() {
        return comparator;
    }

    public MemoryCalculator<T> memoryCalculator() {
        return memoryCalculator;
    }


    public void sort(Sorter<T> sorter) {
        sorter.sort(this);
    }
}
