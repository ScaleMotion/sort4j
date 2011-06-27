package sort4j.text;

import sort4j.DataInputFormat;
import sort4j.DataOutputFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Implementation of {@link DataOutputFormat} that writes data to text file
 * dividing lines by line breaks
 */
public class TextOutputFormat extends TextFormat implements DataOutputFormat<String> {
    private static final String DEFAULT_LINE_BREAK = "\n";
    private String lineBreak = DEFAULT_LINE_BREAK;


    /**
     * @param lineBreak line break (\n by default)
     * @param charset charset
     */
    public TextOutputFormat(String lineBreak, String charset) {
        super(charset);
        this.lineBreak = lineBreak;
    }

    /**
     * @param charset you might want to set charset. Otherwise UTF-8 will be used
     */
    public TextOutputFormat(String charset) {
        this(charset, DEFAULT_LINE_BREAK);
    }

    /**
     * Default constructor
     */
    public TextOutputFormat() {
        this(DEFAULT_LINE_BREAK, DEFAULT_CHARSET);
    }

    /**
     * {@inheritDoc}
     */
    public Writer<String> initialize(final OutputStream output) {
        return new Writer<String>() {
            byte [] lineBreakBytes;
            Charset charsetObj;
            {
                try {
                   charsetObj = Charset.forName(charset);
                   lineBreakBytes = TextOutputFormat.this.lineBreak.getBytes(charset);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            public void write(String item) throws IOException {
                output.write(item.getBytes(charsetObj));
                output.write(lineBreakBytes);
            }

            public void close() throws IOException {
                output.close();
            }
        };
    }

    /**
     * @param lineBreak line break sequence
     */
    public void setLineBreak(String lineBreak) {
        this.lineBreak = lineBreak;
    }

}
