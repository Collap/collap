package io.collap.controller.provider;

import io.collap.template.TemplateRenderer;

public interface JadeDependant extends Dependant {

    public void setRenderer (TemplateRenderer renderer);

}
