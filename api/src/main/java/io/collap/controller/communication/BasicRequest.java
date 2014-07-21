package io.collap.controller.communication;

import io.collap.util.ParseUtils;

public abstract class BasicRequest implements Request {

    protected Request.Method method;

    public BasicRequest (Method method) {
        this.method = method;
    }

    @Override
    public Method getMethod () {
        return method;
    }

    public Long getLongParameter (String name) {
        String value = (String) getParameter (name);
        return ParseUtils.parseLong (value);
    }

    public String getStringParameter (String name) {
        return (String) getParameter (name);
    }

}
