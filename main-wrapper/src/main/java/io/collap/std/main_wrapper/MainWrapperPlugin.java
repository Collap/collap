package io.collap.std.main_wrapper;

import io.collap.resource.TemplatePlugin;

public class MainWrapperPlugin extends TemplatePlugin {

    @Override
    public void initialize () {
        super.initialize ();

        collap.getRootDispatcher ().setWrapper (new MainWrapper (this));
    }

    @Override
    public void destroy () {

    }

}
