package sort4j;

import org.junit.Assert;
import org.junit.Test;
import sort4j.text.StringMemoryCalculator;
import sort4j.text.TextInputFormat;
import sort4j.text.TextOutputFormat;

import java.io.File;

public class MergeSorterTest {
    @Test
    public void testSort() throws Exception {
        File input = new File(getClass().getResource("/test.txt").getFile());
        File output = new File(input.getParent(), "output.txt");
        File tmp = new File(input.getParent(), "tmp");
        SortingTask<String> task = new SortingTask<String>()
                .inputFile(input.getAbsolutePath())
                .outputFile(output.getAbsolutePath())
                .inputFormat(new TextInputFormat())
                .outputFormat(new TextOutputFormat())
                .memoryCalculator(new StringMemoryCalculator());
        new MergeSorter<String>(tmp.getAbsolutePath(), 2, 20).sort(task);
        Assert.assertTrue(output.exists());


    }
}
