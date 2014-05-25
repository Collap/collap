package io.collap.controller.communication;

import java.io.Writer;

public abstract class Response {

    private HttpStatus status;

    /** Applicable in case of an error. */
    private String statusMessage;

    protected Response () {
        status = HttpStatus.ok;
        statusMessage = "";
    }

    protected Response (HttpStatus status) {
        this.status = status;
    }

    public abstract Writer getWriter ();

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
