package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

public interface Controller {

    /**
     * When this method is called, it is guaranteed that a transaction is currently in action and the current session
     * stored in the SessionFactory on a per-thread basis is active.
     */
    public void execute (boolean useWrapper, String remainingPath, Request request, Response response) throws IOException;

    /**
     * The Controller is given the opportunity to handle an error individually. If and only if 'false' is returned,
     * the default error handling is executed (once the Dispatcher chain reaches the root).
     * @return Whether the error was processed successfully.
     */
    public boolean handleError (Request request, Response response) throws IOException;

}
