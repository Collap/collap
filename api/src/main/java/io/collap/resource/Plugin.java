package io.collap.resource;

import org.hibernate.cfg.Configuration;

public abstract class Plugin {

    private boolean initialized = false;
    protected String name;

    public final void initializeWithCheck () {
        if (!initialized) {
            initialize ();
            initialized = true;
        }
    }

    public abstract void initialize ();
    public abstract void destroy ();

    /**
     * This method is called before the main session factory is created.
     * It is called *before* initialize.
     */
    public void configureHibernate (Configuration cfg) { }

    public final String getName () {
        return name;
    }

    public final void setName (String name) {
        this.name = name;
    }

}
