package io.collap.cache;

import io.collap.plugin.Module;

public abstract class BasicInvalidator implements Invalidator {

    protected Module module;

    public BasicInvalidator (Module module) {
        this.module = module;
    }

}
