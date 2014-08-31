package io.collap.controller;

import io.collap.controller.provider.ModuleDependant;
import io.collap.plugin.Module;

public abstract class ModuleController extends BasicController implements ModuleDependant {

    protected Module module;

    @Override
    public void setModule (Module module) {
        this.module = module;
    }

}
