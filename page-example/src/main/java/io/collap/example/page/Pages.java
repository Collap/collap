package io.collap.example.page;

import io.collap.Collap;
import io.collap.controller.Controller;
import io.collap.controller.Dispatcher;
import io.collap.resource.Plugin;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Pages extends Plugin {

    @Override
    public void initialize () {
        Controller controllerMarco = new Controller () {
            @Override
            public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
                WebContext context = new WebContext (request, response, request.getServletContext (), request.getLocale ());
                Collap.getInstance ().getTemplateEngine ().process (getTemplatePath ("Marco"), context, response.getWriter ());
            }
        };
        Controller controllerDamien = new Controller () {
            @Override
            public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
                WebContext context = new WebContext (request, response, request.getServletContext (), request.getLocale ());
                Collap.getInstance ().getTemplateEngine ().process (getTemplatePath ("Damien"), context, response.getWriter ());
            }
        };
        Controller controllerDefault = new Controller () {
            @Override
            public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
                WebContext context = new WebContext (request, response, request.getServletContext (), request.getLocale ());
                Collap.getInstance ().getTemplateEngine ().process (getTemplatePath ("NotFound"), context, response.getWriter ());
            }
        };
        Dispatcher userDispatcher = new Dispatcher (controllerDefault);
        userDispatcher.registerController ("marco", controllerMarco);
        userDispatcher.registerController ("damien", controllerDamien);
        Collap.getInstance ().getRootDispatcher ().registerController ("page", userDispatcher);
    }

    private String getTemplatePath (String path) {
        return name + "/template/page/" + path;
    }

    @Override
    public void destroy () {

    }

}
