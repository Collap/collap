package io.collap.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class Controller {

    public enum Type {
        get,
        post
    }

    public abstract void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
