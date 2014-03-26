package io.collap.app.routing;

import io.collap.Collap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/*
 * Every path is mapped to this servlet.
 * This servlet calls the appropriate Controller that is mapped to the url.
 */
public class Router extends HttpServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType ("text/html;charset=UTF-8");

        // TODO: Only do this in dev mode
        response.setHeader ("Pragma", "no-cache");
        response.setHeader ("Cache-Control", "no-cache");
        response.setDateHeader ("Expires", 0);

        /* Dispatch request. */
        String contextServletPart = request.getContextPath () + request.getServletPath () + "/";
        String requestPart = request.getRequestURI ().substring (contextServletPart.length ()); /* Remove the already used path info. */
        Collap.getInstance ().getRootDispatcher ().execute (requestPart, request, response);
    }

}
