package io.collap.controller.provider;

import io.collap.template.TemplateRenderer;

public interface JadeProvider extends Provider {

    public TemplateRenderer getRenderer ();

}
