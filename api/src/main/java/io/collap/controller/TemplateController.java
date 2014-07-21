package io.collap.controller;

import io.collap.template.TemplateRenderer;

public abstract class TemplateController extends BasicController {

    protected TemplateRenderer renderer;

    public void setRenderer (TemplateRenderer renderer) {
        this.renderer = renderer;
    }

}
