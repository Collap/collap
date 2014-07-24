package io.collap.std.main_wrapper;

import io.collap.plugin.Module;
import io.collap.template.TemplateRenderer;
import org.hibernate.cfg.Configuration;

public class MainWrapperModule extends Module {

    private TemplateRenderer renderer;

    @Override
    public void configureHibernate (Configuration configuration) {

    }

    @Override
    public void initialize () {
        renderer = new TemplateRenderer (this);
        collap.getRootDispatcher ().setWrapper (new MainWrapper (renderer));
    }

    @Override
    public void destroy () {

    }

}
