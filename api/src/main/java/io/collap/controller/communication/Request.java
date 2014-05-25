package io.collap.controller.communication;

import javax.servlet.http.HttpServletRequest;

public class Request {

    public enum Method {
        get,
        post
    }

    private Method method;
    private HttpServletRequest httpRequest;

    public Request (HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;

        /* Find out the request method. */
        method = Method.valueOf (httpRequest.getMethod ().toLowerCase ());
    }

    public Method getMethod () {
        return method;
    }

    public void setMethod (Method method) {
        this.method = method;
    }

    public HttpServletRequest getHttpRequest () {
        return httpRequest;
    }

    public void setHttpRequest (HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

}
