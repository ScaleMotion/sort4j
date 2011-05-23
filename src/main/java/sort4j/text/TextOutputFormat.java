package sort4j.text;

import sort4j.DataInputFormat;
import sort4j.DataOutputFormat;

import java.io.IOException;
import java.io.OutputStream;

public class TextOutputFormat implements DataOutputFormat<String> {
    private static final byte[] LINE_BREAK = "\n".getBytes();

    public Writer<String> initialize(final OutputStream output) {
        return new Writer<String>() {
            public void write(String item) throws IOException {
                output.write(item.getBytes());
                output.write(LINE_BREAK);
            }

            public void close() throws IOException {
                output.close();
            }
        };
    }

}
