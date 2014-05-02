package io.collap.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Controller {

    public enum Type {
        get,
        post
    }

    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
