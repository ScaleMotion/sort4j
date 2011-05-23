package sort4j;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MergeSorter<T> implements Sorter<T> {
    private static final Logger LOG = Logger.getLogger(MergeSorter.class);
    private int executionThreads = Runtime.getRuntime().availableProcessors();
    private int memoryBufferBytes;

    public MergeSorter(int threads, int memoryBufferBytes) {
        this.memoryBufferBytes = memoryBufferBytes;
        executionThreads = threads;
    }

    public void sort(final SortingTask<T> task) {
        final ExecutorService readPool = Executors.newFixedThreadPool(executionThreads);
        final List<File> filesToMerge = new ArrayList<File>();
        for (final File f : task.inputFiles()) {
            FileInputStream s;
            DataInputFormat.Reader<T> reader = null;
            try {
                s = new FileInputStream(f);
                reader = task.inputFormat().initialize(s);
                readInputFile(reader, filesToMerge, task, readPool);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    private void readInputFile(DataInputFormat.Reader<T> reader, List<File> filesToMerge, SortingTask<T> task, ExecutorService readPool) throws Exception {
        List<T> buffer = new ArrayList<T>();
        int bufferSize = 0;
        while (reader.hasNext()) {
            final T item = reader.nextItem();
            buffer.add(item);
            bufferSize += task.memoryCalculator().sizeof(item);
            if (bufferSize >= memoryBufferBytes) {
                filesToMerge.add(flush(buffer, task, readPool));
            }
        }
    }

    private File flush(final List<T> buffer, final SortingTask<T> task, ExecutorService execPool) throws Exception {
        return execPool.submit(new Callable<File>() {
            public File call() throws Exception {
                final File file = new File(task.temporaryDirectory(), Math.random() + "");
                final DataOutputFormat.Writer<T> writer = task.outputFormat().initialize(new FileOutputStream(file));
                try {
                    Collections.sort(buffer, task.comparator());
                    writer.close();
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        //
                    }
                }
                return file;
            }
        }).get();
    }
}
