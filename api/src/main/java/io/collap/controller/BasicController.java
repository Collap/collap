package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

public abstract class BasicController implements Controller {

    @Override
    public boolean handleError (Request request, Response response) throws IOException {
        return false;
    }

}
