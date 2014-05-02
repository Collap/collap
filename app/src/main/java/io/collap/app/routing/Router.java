package io.collap.app.routing;

import io.collap.Collap;
import io.collap.controller.Controller;

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
        configureResponse (response);
        dispatch (Controller.Type.get, request, response);
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configureResponse (response);
        dispatch (Controller.Type.post, request, response);
    }

    private void dispatch (Controller.Type type, HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException{
        Collap.getInstance ().getRootDispatcher ().execute (type, getRemainingRequestPart (request), request, response);
    }

    private void configureResponse (HttpServletResponse response) {
        response.setContentType ("text/html;charset=UTF-8");

        // TODO: Only do this in dev mode
        response.setHeader ("Pragma", "no-cache");
        response.setHeader ("Cache-Control", "no-cache");
        response.setDateHeader ("Expires", 0);
    }

    private String getRemainingRequestPart (HttpServletRequest request) {
        String contextServletPart = request.getContextPath () + request.getServletPath () + "/";
        return request.getRequestURI ().substring (contextServletPart.length ()); /* Remove the already used path info. */
    }

}
