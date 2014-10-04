package io.collap.controller.communication;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class InternalRequest extends BasicRequest {

    protected @Nullable HttpSession session;
    protected Map<String, Object> parameters = new HashMap<> ();
    protected Map<String, Object> sessionAttributes = new HashMap<> ();

    public InternalRequest (Method method) {
        super (method);
        this.session = null;
    }

    public InternalRequest (Method method, HttpSession session) {
        super (method);
        this.session = session;
    }

    @Override
    @Nullable
    public Object getParameter (String name) {
        return parameters.get (name);
    }


    public void setParameter (String name, Object value) {
        parameters.put (name, value);
    }

    /**
     * This method guarantees to return an attribute in the following priority:
     * - From the internal map.
     * - From the session object
     * - Otherwise null
     */
    @Override
    @Nullable
    public Object getSessionAttribute (String name) {
        Object attribute = sessionAttributes.get (name);
        if (attribute != null) {
            return attribute;
        }

        if (session != null) {
            return session.getAttribute (name);
        }

        return null;
    }

    @Override
    public void setSessionAttribute (String name, Object value) {
        sessionAttributes.put (name, value);
    }

    @Nullable
    @Override
    public HttpSession getHttpSession () {
        return session;
    }

}
