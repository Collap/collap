package io.collap.resource;

public abstract class Plugin {

    private boolean initialized = false;
    protected String name;

    public Class<?>[] getEntityClasses () {
        return new Class<?>[0];
    }

    public final void initializeWithCheck () {
        if (!initialized) {
            initialize ();
            initialized = true;
        }
    }

    public abstract void initialize ();
    public abstract void destroy ();

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

}
