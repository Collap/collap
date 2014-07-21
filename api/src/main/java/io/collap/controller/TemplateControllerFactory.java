package io.collap.controller;

import io.collap.resource.Plugin;
import io.collap.template.TemplateRenderer;

public class TemplateControllerFactory extends BasicControllerFactory {

    protected TemplateRenderer renderer;

    public TemplateControllerFactory (Class<? extends Controller> controllerClass, Plugin plugin, TemplateRenderer renderer) {
        super (controllerClass, plugin);
        this.renderer = renderer;
    }

    @Override
    protected void configureController (Controller controller) {
        super.configureController (controller);
        ((TemplateController) controller).setRenderer (renderer);
    }

}
