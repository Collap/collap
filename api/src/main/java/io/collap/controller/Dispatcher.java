package io.collap.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * The Dispatcher acts as a controller that dispatches the request to a sub-controller
 *   or a default controller.
 */
public class Dispatcher extends Controller {

    private HashMap<String, Controller> controllers;

    /**
     * This controller is executed when the dispatcher is the last element in the
     * command chain (i.e. nothing after its name in the remaining path).
     * When this is null, a page not found error is sent back.
     */
    private Controller defaultController;

    public Dispatcher () {
        this.defaultController = null;
        init ();
    }

    public Dispatcher (Controller defaultController) {
        this.defaultController = defaultController;
        init ();
    }

    private void init () {
        controllers = new HashMap<> ();
    }

    public void registerController (String name, Controller controller) {
        // TODO: Handle "already existing" conflicts
        controllers.put (name, controller);
    }

    @Override
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        /* Extract the next controller name. */
        int substringEnd = -1;
        {
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
            controller.execute (type, remainingPath, request, response);
        }else {
            response.sendError (404);
        }
    }

    public Controller getDefaultController () {
        return defaultController;
    }

    public void setDefaultController (Controller defaultController) {
        this.defaultController = defaultController;
    }

}
