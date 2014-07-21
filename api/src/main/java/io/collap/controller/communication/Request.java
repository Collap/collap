package io.collap.controller.communication;

public interface Request {

    public enum Method {
        get,
        post
    }

    public Method getMethod ();

    public Object getParameter (String name);

    /**
     * @return A Long value saved in the parameter, or null when the parameter was not found or the
     *          Long was not properly formatted.
     */
    public Long getLongParameter (String name);

    public String getStringParameter (String name);

    public Object getSessionAttribute (String name);
    public void setSessionAttribute (String name, Object value);

}
