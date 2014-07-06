package io.collap.cache;

import io.collap.controller.communication.Request;

public interface Cached {

    public boolean isRequestMethodCached (Request.Method method);
    public String getElementKey (String remainingPath, Request request);

}
