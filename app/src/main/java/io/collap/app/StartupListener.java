package io.collap.app;

import io.collap.Collap;

import javax.servlet.*;

public class StartupListener implements javax.servlet.ServletContextListener {

    @Override
    public void contextInitialized (ServletContextEvent sce) {
        Collap.getInstance ().initialize ();
    }

    @Override
    public void contextDestroyed (ServletContextEvent sce) {
        Collap.getInstance ().destroy ();
    }

}