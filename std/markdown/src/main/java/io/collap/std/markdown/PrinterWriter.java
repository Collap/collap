package io.collap.std.markdown;

import org.pegdown.Printer;

import java.io.IOException;
import java.io.Writer;

public class PrinterWriter extends Writer {

    private Printer printer;

    public PrinterWriter (Printer printer) {
        this.printer = printer;
    }

    @Override
    public void write (char[] cbuf, int off, int len) throws IOException {
        String text = new String (cbuf, off, len);
        printer.sb.append (text);
    }

    @Override
    public void flush () throws IOException {
        /* Not needed. */
    }

    @Override
    public void close () throws IOException {
        /* Not needed. */
    }

}
