package io.collap.controller.provider;

import io.collap.plugin.Module;

public interface ModuleDependant extends Dependant {

    public void setModule (Module module);

}
