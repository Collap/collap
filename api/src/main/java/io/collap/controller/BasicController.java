package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.plugin.Module;

import java.io.IOException;

/**
 * This class also handles standard fragment caching!
 */
public abstract class BasicController implements Controller {

    protected Module module;
    protected Request request;

    @Override
    public void initialize (String remainingPath, Request request) {
        this.request = request;
        initialize (remainingPath);
    }

    /**
     * This method is part of the initialization process.
     */
    public abstract void initialize (String remainingPath);

    @Override
    public void doGet (Response response) throws IOException{
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

    public void setModule (Module module) {
        this.module = module;
    }

}
