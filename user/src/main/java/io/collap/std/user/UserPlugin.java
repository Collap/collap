package io.collap.std.user;

import io.collap.controller.Dispatcher;
import io.collap.resource.JadePlugin;
import io.collap.std.entity.User;
import io.collap.std.user.page.Register;
import org.hibernate.cfg.Configuration;

public class UserPlugin extends JadePlugin {

    @Override
    public void initialize () {
        super.initialize ();

        Dispatcher userDispatcher = new Dispatcher ();
        userDispatcher.registerController ("register", new Register (this));
        collap.getRootDispatcher ().registerController ("user", userDispatcher);
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (User.class);
    }

}
