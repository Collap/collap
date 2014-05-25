package io.collap.controller.communication;

import io.collap.util.ParseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    /**
     * Shortcut method for HttpServletRequest.getSession.
     */
    public HttpSession getHttpSession () {
        return httpRequest.getSession ();
    }

    /**
     * Shortcut method for HttpServletRequest.getSession (boolean).
     */
    public HttpSession getHttpSession (boolean create) {
        return httpRequest.getSession (create);
    }

    /**
     * @return A Long value saved in the parameter, or null when the parameter was not found or the
     *          Long was not properly formatted.
     */
    public Long getLongParameter (String name) {
        String value = httpRequest.getParameter (name);
        return ParseUtils.parseLong (value);
    }

    public String getStringParameter (String name) {
        return httpRequest.getParameter (name);
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
