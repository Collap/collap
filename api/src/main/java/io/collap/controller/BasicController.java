package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

public abstract class BasicController implements Controller {

    protected Request request;

    @Override
    public void initialize (Request request, String remainingPath) {
        this.request = request;
    }

    @Override
    public void doGet (Response response) throws IOException {
        throw new NoSuchMethodError ("This controller does not support GET requests!");
    }

    @Override
    public void doPost (Response response) throws IOException {
        throw new NoSuchMethodError ("This controller does not support POST requests!");
    }

    @Override
    public boolean handleError (Response response) throws IOException {
        return false;
    }

}
