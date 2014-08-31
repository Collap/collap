package io.collap.controller;

import io.collap.Collap;
import io.collap.cache.Cached;
import io.collap.cache.Fragment;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import net.sf.ehcache.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The Dispatcher forwards requests to a dispatcher or controller.
 */
public class Dispatcher {

    private static final Logger logger = Logger.getLogger (Dispatcher.class.getName ());

    private Collap collap;

    // TODO: A controller and dispatcher could share a name, this is not intended!
    private Map<String, ControllerFactory> controllerFactories = new HashMap<> ();
    private Map<String, Dispatcher> dispatchers = new HashMap<> ();

    /**
     * The wrapper of the current dispatcher. The wrapper is passed down to any child
     * dispatchers, which use that if their own wrapper is null.
     */
    private Wrapper wrapper;

    /**
     * This controller is executed when the dispatcher is the last element in the
     * command chain (i.e. nothing after its name in the remaining path).
     * When this is null, a page not found error is sent back.
     */
    private ControllerFactory defaultControllerFactory;

    public Dispatcher (Collap collap) {
        this (collap, null);
    }

    public Dispatcher (Collap collap, ControllerFactory defaultControllerFactory) {
        this.collap = collap;
        this.defaultControllerFactory = defaultControllerFactory;
    }

    public void registerControllerFactory (String name, ControllerFactory controllerFactory) {
        // TODO: Handle "already existing" conflicts
        controllerFactories.put (name, controllerFactory);
    }

    public void registerDispatcher (String name, Dispatcher dispatcher) {
        dispatchers.put (name, dispatcher);
    }

    public void execute (Wrapper parentWrapper, boolean useWrapper, String remainingPath, Request request, Response response) throws IOException {
        /* Extract the next dispatcher/controller name. */
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

        boolean controllerNameEmpty = controllerName.isEmpty ();

        Wrapper usedWrapper = (wrapper != null) ? wrapper : parentWrapper;

        /* Find the appropriate dispatcher. */
        if (!controllerNameEmpty) {
            Dispatcher dispatcher = dispatchers.get (controllerName);
            if (dispatcher != null) {
                dispatcher.execute (usedWrapper, useWrapper, remainingPath, request, response);
                return;
            }
        }

        /* Find the appropriate controller factory, create a controller object and execute the controller. */
        ControllerFactory controllerFactory;
        if (controllerNameEmpty) { /* The dispatcher is last in the command chain. */
            controllerFactory = defaultControllerFactory;
        }else {
            controllerFactory = controllerFactories.get (controllerName);
        }

        /* Execute the controller or throw a 404 error. */
        if (controllerFactory != null) {
            Controller controller = controllerFactory.createController ();

            Response controllerResponse;

            if (usedWrapper != null) {
                controllerResponse = new Response ();
            }else {
                controllerResponse = response;
            }

            // TODO: Rework status handling (Currently the controller status is not passed to the wrapper!).
            executeController (controller, remainingPath, request, controllerResponse);
            if (controllerResponse.getStatus () != HttpStatus.ok) {
                if (controller.handleError (controllerResponse)) {
                    /* The error has been handled. */
                    controllerResponse.setStatus (HttpStatus.ok);
                }
            }

            if (usedWrapper != null) {
                usedWrapper.execute (controllerResponse, request, response);
            }
        }else {
            response.setStatus (HttpStatus.notFound);
        }
    }

    private void executeController (Controller controller, String remainingPath, Request request, Response response) throws IOException {
        controller.initialize (remainingPath, request);

        boolean useCache = false;
        String key = null;
        if (controller instanceof Cached) {
            Cached cached = (Cached) controller;
            useCache = cached.shouldResponseBeCached ();
            if (useCache) {
                key = cached.getElementKey ();
            }
        }

        /* Respond with cached element. */
        if (useCache) {
            Element element = collap.getFragmentCache ().get (key);
            if (element != null) {
                Fragment fragment = (Fragment) element.getObjectValue ();
                response.getHeadWriter ().write (fragment.getHead ());
                response.getContentWriter ().write ("Fetched from cache with key '" + key + "'<br>");
                response.getContentWriter ().write (fragment.getContent ());
                return;
            }
        }

        if (request.getMethod () == Request.Method.get) {
            controller.doGet (response);
        }else if (request.getMethod () == Request.Method.post) {
            controller.doPost (response);
        }else {
            throw new UnsupportedOperationException ("The controller does not support the " + request.getMethod ()
                + " request method!");
        }

        /* Add element to cache! */
        if (useCache) {
            Fragment fragment = new Fragment (response.getHead (), response.getContent ());
            Element element = new Element (key, fragment);
            collap.getFragmentCache ().put (element);
        }
    }

    public ControllerFactory getDefaultControllerFactory () {
        return defaultControllerFactory;
    }

    public void setDefaultControllerFactory (ControllerFactory defaultControllerFactory) {
        this.defaultControllerFactory = defaultControllerFactory;
    }

    public Wrapper getWrapper () {
        return wrapper;
    }

    public void setWrapper (Wrapper wrapper) {
        this.wrapper = wrapper;
    }

}
