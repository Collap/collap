package io.collap.controller;

import io.collap.resource.TemplatePlugin;

public abstract class TemplateController extends Controller {

    protected TemplatePlugin plugin;

    protected TemplateController (TemplatePlugin plugin) {
        this.plugin = plugin;
    }

    public TemplatePlugin getPlugin () {
        return plugin;
    }

}
