package io.collap.std.user;

import io.collap.controller.Dispatcher;
import io.collap.controller.SectionControllerFactory;
import io.collap.controller.TemplateControllerFactory;
import io.collap.plugin.Module;
import io.collap.std.user.entity.User;
import io.collap.std.user.page.Login;
import io.collap.std.user.page.Profile;
import io.collap.std.user.page.Register;
import io.collap.std.user.util.Validator;
import io.collap.template.TemplateRenderer;
import org.hibernate.cfg.Configuration;

public class UserModule extends Module {

    private TemplateRenderer renderer;
    private Validator validator;
    private SectionControllerFactory profileSectionControllerFactory;

    @Override
    public void initialize () {
        renderer = new TemplateRenderer (this);
        validator = new Validator ();
        profileSectionControllerFactory = new SectionControllerFactory (Profile.class, this, renderer);

        Dispatcher userDispatcher = new Dispatcher (collap);
        userDispatcher.registerControllerFactory ("register", new TemplateControllerFactory (Register.class, this, renderer));
        userDispatcher.registerControllerFactory ("login", new TemplateControllerFactory (Login.class, this, renderer));
        userDispatcher.registerControllerFactory ("profile", profileSectionControllerFactory);
        collap.getRootDispatcher ().registerDispatcher ("user", userDispatcher);
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (User.class);
    }

    public Validator getValidator () {
        return validator;
    }

    public SectionControllerFactory getProfileSectionControllerFactory () {
        return profileSectionControllerFactory;
    }

}
