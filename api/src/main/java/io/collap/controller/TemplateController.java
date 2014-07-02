package io.collap.controller;

import io.collap.resource.TemplatePlugin;

public abstract class TemplateController extends BasicController {

    protected TemplatePlugin plugin;

    protected TemplateController (TemplatePlugin plugin) {
        this.plugin = plugin;
    }

    public TemplatePlugin getPlugin () {
        return plugin;
    }

}
