package io.collap.controller;

import io.collap.resource.TemplatePlugin;

public abstract class TemplateController extends BasicController {

    protected TemplatePlugin plugin;

    protected TemplateController (TemplatePlugin plugin) {
        super (plugin.getCollap ());
        this.plugin = plugin;
    }

    public TemplatePlugin getPlugin () {
        return plugin;
    }

}
