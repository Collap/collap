package io.collap.app.routing;

import io.collap.Collap;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Every path is mapped to this servlet.
 * This servlet calls the appropriate Controller that is mapped to the url.
 */
public class Router extends HttpServlet {

    private static Logger logger = Logger.getLogger (Router.class.getName ());

    /** This field is set once by the StartupListener. */
    public static Collap collap; // TODO: This is ugly, but the only quick solution I can come up with right now.

    @Override
    protected void doGet (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        dispatch (httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatch (request, response);
    }

    private void dispatch (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)  throws ServletException, IOException {
        Request request = new Request (httpServletRequest);
        Response response = new Response ();
        configureServletResponse (httpServletResponse);

        Session session = collap.getSessionFactory ().getCurrentSession ();
        Transaction transaction = session.beginTransaction ();

        // TODO: Cache full response?
        collap.getRootDispatcher ().execute (true, getRemainingRequestPart (httpServletRequest), request, response);
        httpServletResponse.getWriter ().write (response.getContent ()); // TODO: Unnecessary copying.

        try {
            transaction.commit ();
        } catch (HibernateException e) {
            transaction.rollback ();
            String message = "A transaction failed to commit!";
            response.setStatus (HttpStatus.internalServerError);
            response.setStatusMessage (message);
            logger.log (Level.SEVERE, message, e);
            // TODO: Implement controller callback?
        }

        if (response.getStatus () != HttpStatus.ok) {
            httpServletResponse.sendError (response.getStatus ().getValue (), response.getStatusMessage ());
        }
    }

    private void configureServletResponse (HttpServletResponse httpServletResponse) {
        /* Configure HTTP Servlet response. */
        httpServletResponse.setContentType ("text/html;charset=UTF-8");

        // TODO: Only do this in dev mode
        httpServletResponse.setHeader ("Pragma", "no-cache");
        httpServletResponse.setHeader ("Cache-Control", "no-cache");
        httpServletResponse.setDateHeader ("Expires", 0);
    }

    private String getRemainingRequestPart (HttpServletRequest request) {
        String contextServletPart = request.getContextPath () + request.getServletPath () + "/";
        return request.getRequestURI ().substring (contextServletPart.length ()); /* Remove the already used path info. */
    }

}
