package io.collap.controller;

import io.collap.plugin.Module;
import io.collap.template.TemplateRenderer;

public class TemplateControllerFactory extends BasicControllerFactory {

    protected TemplateRenderer renderer;

    public TemplateControllerFactory (Class<? extends Controller> controllerClass, Module module, TemplateRenderer renderer) {
        super (controllerClass, module);
        this.renderer = renderer;
    }

    @Override
    protected void configureController (Controller controller) {
        super.configureController (controller);
        ((TemplateController) controller).setRenderer (renderer);
    }

}
