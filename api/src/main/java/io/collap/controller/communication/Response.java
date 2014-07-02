package io.collap.controller.communication;

import java.io.StringWriter;
import java.io.Writer;

public class Response {

    protected Writer headWriter;
    protected Writer contentWriter;

    private HttpStatus status;

    /** Applicable in case of an error. */
    private String statusMessage;

    public Response () {
        status = HttpStatus.ok;
        statusMessage = "";
    }

    public Response (HttpStatus status) {
        this.status = status;
    }

    public Writer getHeadWriter () {
        if (headWriter == null) {
            headWriter = new StringWriter ();
        }
        return headWriter;
    }

    public String getHead () {
        if (headWriter == null) {
            return "";
        }
        return headWriter.toString ();
    }

    public Writer getContentWriter () {
        if (contentWriter == null) {
            contentWriter = new StringWriter ();
        }
        return contentWriter;
    }

    public String getContent () {
        if (contentWriter == null) {
            return "";
        }
        return contentWriter.toString ();
    }

    public HttpStatus getStatus () {
        return status;
    }

    public void setStatus (HttpStatus status) {
        this.status = status;
    }

    public String getStatusMessage () {
        return statusMessage;
    }

    public void setStatusMessage (String statusMessage) {
        this.statusMessage = statusMessage;
    }

}
