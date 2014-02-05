package io.collap.plugin;

public abstract class Plugin {

    protected String name;

    public Class<?>[] getEntityClasses () {
        return new Class<?>[0];
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
