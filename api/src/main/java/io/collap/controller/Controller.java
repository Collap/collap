package io.collap.controller;

import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;

/**
 * During the lifetime of every reference to this controller, it is guaranteed that a transaction is currently in action
 * and that the current session stored in the SessionFactory on a per-thread basis is active.
 * The implementation of this interface does not need to be thread-safe, as a new Controller is created for every request.
 *
 * The initialization is used to prepare the controller for caching and can possibly be used for other purposes.
 * Database use, general IO use and heavy tasks should not be performed during initialization.
 */
public interface Controller {

    // TODO: Turn "remainingPath" into a proper argument (with a fitting type) to the controller.
    public void initialize (Request request, String remainingPath);

    public void doGet (Response response) throws IOException;

    public void doPost (Response response) throws IOException;

    /**
     * The Controller is given the opportunity to handle an error individually. If and only if 'false' is returned,
     * the default error handling is executed (once the Dispatcher chain reaches the root).
     * @return Whether the error was processed successfully.
     */
    public boolean handleError (Response response) throws IOException;

}
