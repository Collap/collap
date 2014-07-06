package io.collap.controller;

import io.collap.Collap;
import io.collap.cache.Cached;
import io.collap.cache.Fragment;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import net.sf.ehcache.Element;

import java.io.IOException;

/**
 * This class also handles standard fragment caching!
 */
public abstract class BasicController implements Controller {

    private Collap collap;

    protected BasicController (Collap collap) {
        this.collap = collap;
    }

    /**
     * @param useWrapper This parameter is ignored and has no semantics here!
     */
    @Override
    public final void execute (boolean useWrapper, String remainingPath, Request request, Response response) throws IOException {
        boolean useCache = false;
        String key = null;
        if (this instanceof Cached) {
            Cached cached = (Cached) this;
            useCache = cached.isRequestMethodCached (request.getMethod ());
            if (useCache) {
                key = cached.getElementKey (remainingPath, request);
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
            doGet (remainingPath, request, response);
        }else if (request.getMethod () == Request.Method.post) {
            doPost (remainingPath, request, response);
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
