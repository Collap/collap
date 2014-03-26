package io.collap.controller;

import io.collap.Collap;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultController implements Controller {

    @Override
    public void execute (String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebContext context = new WebContext (request, response, request.getServletContext (), request.getLocale ());
        context.setVariable ("path", request.getRequestURI ());
        context.setVariable ("remainingPath", remainingPath);
        Collap.getInstance ().getTemplateEngine ().process ("DefaultController", context, response.getWriter ());
    }

}
