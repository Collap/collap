package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

public abstract class BasicController implements Controller {

    @Override
    public final void execute (boolean useWrapper, String remainingPath, Request request, Response response) throws IOException {
        if (request.getMethod () == Request.Method.get) {
            doGet (remainingPath, request, response);
        }else if (request.getMethod () == Request.Method.post) {
            doPost (remainingPath, request, response);
        }else {
            throw new UnsupportedOperationException ("The controller does not support the " + request.getMethod ()
                + " request method!");
        }
    }

    protected void doGet (String remainingPath, Request request, Response response) throws IOException{
        throw new NoSuchMethodError ("This controller does not support GET requests!");
    }

    protected void doPost (String remainingPath, Request request, Response response) throws IOException {
        throw new NoSuchMethodError ("This controller does not support POST requests!");
    }

    @Override
    public boolean handleError (Request request, Response response) throws IOException {
        return false;
    }

}
