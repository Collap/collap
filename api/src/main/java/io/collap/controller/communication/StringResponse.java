package io.collap.controller.communication;

import java.io.StringWriter;
import java.io.Writer;

public class StringResponse extends Response {

    private StringWriter writer;

    public StringResponse () {
        writer = new StringWriter ();
    }

    @Override
    public Writer getWriter () {
        return writer;
    }

    public String getContent () {
        return writer.toString ();
    }

}
