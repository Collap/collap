package io.collap.controller.communication;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpRequest extends BasicRequest {

    private HttpServletRequest httpRequest;

    public HttpRequest (HttpServletRequest httpRequest) {
        super (Method.valueOf (httpRequest.getMethod ().toLowerCase ()));

        this.httpRequest = httpRequest;
    }

    @Override
    public String getParameter (String name) {
        return httpRequest.getParameter (name);
    }

    @Override
    public Object getSessionAttribute (String name) {
        HttpSession httpSession = httpRequest.getSession ();
        if (httpSession == null) {
            return null;
        }
        return httpSession.getAttribute (name);
    }

    @Override
    public void setSessionAttribute (String name, Object value) {
        HttpSession httpSession = httpRequest.getSession (true);
        httpSession.setAttribute (name, value);
    }

    @Nullable
    @Override
    public HttpSession getHttpSession () {
        return httpRequest.getSession ();
    }

}
