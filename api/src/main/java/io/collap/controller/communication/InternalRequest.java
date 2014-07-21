package io.collap.controller.communication;

import java.util.HashMap;
import java.util.Map;

public class InternalRequest extends BasicRequest {

    protected Map<String, Object> parameters = new HashMap<> ();
    protected Map<String, Object> sessionAttributes = new HashMap<> ();

    public InternalRequest (Method method) {
        super (method);
    }

    @Override
    public Object getParameter (String name) {
        return parameters.get (name);
    }


    public void setParameter (String name, Object value) {
        parameters.put (name, value);
    }

    @Override
    public Object getSessionAttribute (String name) {
        return sessionAttributes.get (name);
    }

    @Override
    public void setSessionAttribute (String name, Object value) {
        sessionAttributes.put (name, value);
    }

}
