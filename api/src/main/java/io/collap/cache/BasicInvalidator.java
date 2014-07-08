package io.collap.cache;

import io.collap.resource.Plugin;

public abstract class BasicInvalidator implements Invalidator {

    protected Plugin plugin;

    public BasicInvalidator (Plugin plugin) {
        this.plugin = plugin;
    }

}
