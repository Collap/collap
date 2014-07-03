package io.collap.controller;

import io.collap.Collap;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Dispatcher acts as a controller that dispatches the request to a sub-controller
 *   or a default controller.
 */
public class Dispatcher implements Controller {

    private static final Logger logger = Logger.getLogger (Dispatcher.class.getName ());

    private Collap collap;

    private Map<String, Controller> controllers = new HashMap<> ();

    private Wrapper wrapper;

    /**
     * This controller is executed when the dispatcher is the last element in the
     * command chain (i.e. nothing after its name in the remaining path).
     * When this is null, a page not found error is sent back.
     */
    private Controller defaultController;

    public Dispatcher (Collap collap) {
        this (collap, null);
    }

    public Dispatcher (Collap collap, Controller defaultController) {
        this.collap = collap;
        this.defaultController = defaultController;
    }

    public void registerController (String name, Controller controller) {
        // TODO: Handle "already existing" conflicts
        controllers.put (name, controller);
    }

    @Override
    public void execute (boolean useWrapper, String remainingPath, Request request, Response response) throws IOException {
        /* Extract the next controller name. */
        int substringEnd = -1;
        int nextSlash = remainingPath.indexOf ('/'); /* Until next controller name. */
        if (nextSlash >= 0) {
            substringEnd = nextSlash;
        }else {
            int queryPos = remainingPath.indexOf ('?'); /* Until query string. */
            if (queryPos >= 0) {
                substringEnd = queryPos;
            }else {
                int fragmentPos = remainingPath.indexOf ('#'); /* Until fragment string. */
                if (fragmentPos >= 0) {
                    substringEnd = fragmentPos;
                }
            }
        }

        String controllerName;
        if (substringEnd == -1) {
            controllerName = remainingPath;
            remainingPath = "";
        }else {
            controllerName = remainingPath.substring (0, substringEnd);
            remainingPath = remainingPath.substring (substringEnd);
            /* Remove trailing slash. */
            if (remainingPath.charAt (0) == '/') {
                remainingPath = remainingPath.substring (1);
            }
        }

        /* Find the appropriate controller. */
        Controller controller;
        if (controllerName.length () == 0) { /* The dispatcher is the last controller in the command chain. */
            controller = defaultController;
        }else {
            controller = controllers.get (controllerName);
        }

        /* Execute the controller or throw a 404 error. */
        if (controller != null) {
            Response controllerResponse;
            if (wrapper != null) {
                controllerResponse = new Response ();
            }else {
                controllerResponse = response;
            }

            // TODO: Rework status handling (Currently the controller status is not passed to the wrapper!).
            controller.execute (useWrapper, remainingPath, request, controllerResponse);
            if (controllerResponse.getStatus () != HttpStatus.ok) {
                if (controller.handleError (request, controllerResponse)) {
                    /* The error has been handled. */
                    controllerResponse.setStatus (HttpStatus.ok);
                }
            }

            if (wrapper != null) {
                wrapper.execute (controllerResponse, request, response);
            }
        }else {
            response.setStatus (HttpStatus.notFound);
        }
    }

    @Override
    public boolean handleError (Request request, Response response) throws IOException {
        // TODO: Implement.
        return false;
    }

    public Controller getDefaultController () {
        return defaultController;
    }

    public void setDefaultController (BasicController defaultController) {
        this.defaultController = defaultController;
    }

    public Wrapper getWrapper () {
        return wrapper;
    }

    public void setWrapper (Wrapper wrapper) {
        this.wrapper = wrapper;
    }

}
