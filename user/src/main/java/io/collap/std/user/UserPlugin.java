package io.collap.std.user;

import io.collap.controller.Controller;
import io.collap.controller.Dispatcher;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.User;
import io.collap.std.user.page.Login;
import io.collap.std.user.page.Profile;
import io.collap.std.user.page.Register;
import io.collap.std.user.util.Validator;
import org.hibernate.cfg.Configuration;

public class UserPlugin extends TemplatePlugin {

    private Validator validator;

    @Override
    public void initialize () {
        super.initialize ();

        validator = new Validator ();

        Dispatcher userDispatcher = new Dispatcher ();
        userDispatcher.registerController ("register", new Register (this));
        userDispatcher.registerController ("login", new Login (this));
        userDispatcher.registerController ("profile", new Profile (this));
        collap.getRootDispatcher ().registerController ("user", userDispatcher);
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

}
