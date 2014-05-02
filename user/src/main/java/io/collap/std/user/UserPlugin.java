package io.collap.std.user;

import io.collap.Collap;
import io.collap.controller.Dispatcher;
import io.collap.resource.Plugin;
import io.collap.std.entity.User;
import io.collap.std.user.page.Register;
import org.hibernate.cfg.Configuration;

public class UserPlugin extends Plugin {

    @Override
    public void initialize () {
        Dispatcher userDispatcher = new Dispatcher ();
        userDispatcher.registerController ("register", new Register (this));
        Collap.getInstance ().getRootDispatcher ().registerController ("user", userDispatcher);
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (User.class);
    }

}
