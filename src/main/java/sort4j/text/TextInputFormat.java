package sort4j.text;

import sort4j.DataInputFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;

public class TextInputFormat extends TextFormat implements DataInputFormat<String> {
    /**
     * Default constructor
     */
    public TextInputFormat() {
        super(DEFAULT_CHARSET);
    }

    /**
     * {@inheritDoc}
     */
    public TextInputFormat(String charset) {
        super(charset);
    }

    /**
     * {@inheritDoc}
     */
    public Reader<String> initialize(InputStream in) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(this.charset)));
        return new Reader<String>() {
            private String nextLine;
            private boolean nextIsCached = false;
            public boolean hasNext() throws IOException {
                if (!nextIsCached) {
                    nextLine = reader.readLine();
                    nextIsCached = true;
                }
                return nextLine != null;
            }

            public String nextItem() throws IOException {
                if (!hasNext()) {
                    throw new IllegalStateException("End of stream");
                }
                nextIsCached = false;
                return nextLine;
            }
            public void close() throws IOException {
                reader.close();
            }
        };
    }
}
