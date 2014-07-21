package io.collap.std.main_wrapper;

import io.collap.resource.Plugin;
import io.collap.template.TemplateRenderer;

public class MainWrapperPlugin extends Plugin {

    private TemplateRenderer renderer;

    @Override
    public void initialize () {
        renderer = new TemplateRenderer (this);
        collap.getRootDispatcher ().setWrapper (new MainWrapper (renderer));
    }

    @Override
    public void destroy () {

    }

}
