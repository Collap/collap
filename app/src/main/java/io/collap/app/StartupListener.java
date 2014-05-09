package io.collap.app;

import io.collap.Collap;
import io.collap.app.routing.Router;

import javax.servlet.*;

public class StartupListener implements javax.servlet.ServletContextListener {

    private Collap collap;

    @Override
    public void contextInitialized (ServletContextEvent sce) {
        collap = new Collap ();
        Router.collap = collap;
        collap.initialize ();
    }

    @Override
    public void contextDestroyed (ServletContextEvent sce) {
        collap.destroy ();
    }

}