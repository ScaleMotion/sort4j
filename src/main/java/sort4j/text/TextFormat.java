package sort4j.text;

class TextFormat {
    protected static final String DEFAULT_CHARSET = "UTF-8";
    protected String charset = DEFAULT_CHARSET;

    public TextFormat(String charset) {
        this.charset = charset;
    }

    /**
     * @param charset charset name
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
