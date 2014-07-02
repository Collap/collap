package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

/**
 * A Wrapper is executed after a controller is executed. It can be used to implement a "main" page
 * that is common to all controllers.
 */
public interface Wrapper {

    public void execute (Response in, Request request, Response out) throws IOException;

}
