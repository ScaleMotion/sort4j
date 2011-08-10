package com.scalemotion.sort4j;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Merge sort for large files. Divides data into chunks, sorts each small chunk in memory
 * and then merges them. memoryBufferBytes param defines if chunk fits in memory
 * @param <T>
 */
public class MergeSorter<T> implements Sorter<T> {
    private static final Logger LOG = Logger.getLogger(MergeSorter.class);
    private int executionThreads;
    private int memoryBufferBytes;
    private String temporaryDirectory;

    /**
     * @param temporaryDirectory directory where sorter will keep temporary files
     * @param threads amount of working threads
     * @param memoryBufferBytes maximum size of in-memory chunk
     */
    public MergeSorter(String temporaryDirectory, int threads, int memoryBufferBytes) {
        this.memoryBufferBytes = memoryBufferBytes;
        this.executionThreads = threads;
        this.temporaryDirectory = temporaryDirectory;
    }

    /**
     * @param executionThreads amount of threads
     */
    public void setExecutionThreads(int executionThreads) {
        this.executionThreads = executionThreads;
    }

    /**
     * @param memoryBufferBytes maximum amount of in-memory chunk
     */
    public void setMemoryBufferBytes(int memoryBufferBytes) {
        this.memoryBufferBytes = memoryBufferBytes;
    }

    /**
     * @param temporaryDirectory temporary directory
     */
    public void setTemporaryDirectory(String temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
    }
    public void sort(final SortingTask<T> task) {
        new File(temporaryDirectory).mkdirs();
        ExecutorService executorService = Executors.newFixedThreadPool(executionThreads);
        final List<File> filesToMerge = Collections.synchronizedList(new ArrayList<File>());
        final long maxMemoryPerBuffer = memoryBufferBytes / executionThreads;
        final AtomicInteger records = new AtomicInteger();
        for (final String f : task.inputFiles()) {
            executorService.submit(new Runnable() {
                public void run() {
                    List<T> buffer = new ArrayList<T>();
                    int bufferSize = 0;
                    FileInputStream s;
                    DataInputFormat.Reader<T> reader = null;
                    try {
                        s = new FileInputStream(f);
                        reader = task.inputFormat().initialize(s);
                        while (reader.hasNext()) {
                            final T item = reader.nextItem();
                            records.incrementAndGet();
                            buffer.add(item);
                            bufferSize += task.memoryCalculator().sizeof(item);
                            if (bufferSize >= maxMemoryPerBuffer) {
                                final List<T> bufferToFlush = buffer;
                                flush(bufferToFlush, task, filesToMerge);
                                buffer = new ArrayList<T>();
                                bufferSize = 0;
                            }
                        }
                        if (bufferSize != 0) {
                            flush(buffer, task, filesToMerge);
                        }
                    } catch (Exception e) {
                        LOG.warn("Can't read file " + f, e);
                        throw new RuntimeException("Can't read file " + f, e);
                    } finally {
                        closeQuietly(reader);
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            LOG.info(records + " records was sorted (total)");
            merge(filesToMerge, task);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static void closeQuietly(Closeable reader) {
        try {
            reader.close();
        } catch (IOException e) {
            //
        }
    }

    private void merge(List<File> filesToMerge, final SortingTask<T> task) throws IOException {
        LOG.debug("Merging " + filesToMerge.size() + " files");
        if (filesToMerge.isEmpty()) {
			return;
		}

		FileOutputStream output = new FileOutputStream(task.outputFile());
        PriorityQueue<InputProxy> queue = new PriorityQueue<InputProxy>(filesToMerge.size(), new Comparator<InputProxy>() {
            public int compare(InputProxy o1, InputProxy o2) {
                return task.comparator().compare(o1.peek(), o2.peek());
            }
        });
        Set<DataInputFormat.Reader<T>> toClose = new HashSet<DataInputFormat.Reader<T>>();
        for (File f : filesToMerge) {
            final DataInputFormat.Reader<T> reader;
            try {
                reader = task.inputFormat().initialize(new FileInputStream(f));
            } catch (Exception e) {
                throw new RuntimeException("Can't initiailize reader for " + f.getAbsolutePath(), e);
            }
            final InputProxy proxy = new InputProxy(reader);
            assert !proxy.isEmpty();
            queue.add(proxy);
            toClose.add(reader);
        }
        final DataOutputFormat.Writer<T> writer = task.outputFormat().initialize(output);
        try {
            while (!queue.isEmpty()) {
                final InputProxy top = queue.poll();
                final T element = top.pop();
                writer.write(element);
                if (!top.isEmpty()) {
                    queue.add(top);
                }
            }
            writer.close();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                //
            }
        }


        for (File f : filesToMerge) {
            f.delete();
        }

        for (DataInputFormat.Reader<T> r : toClose) {
            try {
                r.close();
            } catch (Exception e) {
                //
            }
        }
    }

    private void flush(final List<T> buffer, final SortingTask<T> task, final List<File> filesToMerge) throws Exception {
        if (buffer.isEmpty()) {
            throw new IllegalStateException("Can't flush empty buffer");
        }
        final File file = new File(temporaryDirectory, "sort4j-temp-" + Math.random());
        final DataOutputFormat.Writer<T> writer = task.outputFormat().initialize(new FileOutputStream(file));
        try {
            long time = System.currentTimeMillis();
            Collections.sort(buffer, task.comparator());
            time = System.currentTimeMillis() - time;
            LOG.debug("Sorting of  " + buffer.size() + " items done in " + time + "ms, flushing sorting result to temporary file");
            time = System.currentTimeMillis();
            for (T i : buffer) {
                writer.write(i);
            }
            writer.close();
            time = System.currentTimeMillis() - time;
            LOG.debug(buffer.size() + " sorted items was written in " + time + "ms");
            buffer.clear();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                //
            }
        }
        filesToMerge.add(file);
    }

    private class InputProxy {
        private DataInputFormat.Reader<T> input;
        private T item;
        private boolean empty = false;

        private InputProxy(DataInputFormat.Reader<T> input) throws IOException {
            this.input = input;
            readNext();
        }

        public T peek() {
            if (empty) {
                throw new IllegalStateException("Can't peek, it's empty");
            }
            return item;
        }

        public T pop() throws IOException {
            if (empty) {
                throw new IllegalStateException("Can't pop, it's empty");
            }
            T pop = item;
            readNext();
            return pop;
        }

        private void readNext() throws IOException {
            if (!input.hasNext()) {
                empty = true;
            } else {
                empty = false;
                item = input.nextItem();
            }
        }

        public T getItem() {
            return item;
        }

        public boolean isEmpty() {
            return empty;
        }
    }
}
