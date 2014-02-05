package io.collap.app.routing;

import io.collap.Collap;
import io.collap.StandardDirectories;
import io.collap.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.thymeleaf.context.WebContext;

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
        response.setHeader ("Pragma", "no-cache");
        response.setHeader ("Cache-Control", "no-cache");
        response.setDateHeader ("Expires", 0);

        WebContext context = new WebContext (request, response, this.getServletContext (), request.getLocale ());

        File file = new File (StandardDirectories.base, "test.txt");
        try {
            file.createNewFile ();
            context.setVariable ("fileState", "File created successfully!");
        }catch (IOException ex) {
            context.setVariable ("fileState", "An error occurred while creating the test file!");
        }

        long time = System.nanoTime ();
        Collap.getInstance ().getTemplateEngine ().process ("test", context, response.getWriter ());
        long diff = System.nanoTime () - time;

        BufferedWriter writer = new BufferedWriter (new FileWriter (file));
        writer.write ("" + diff + "\n");
        writer.write (request.getRequestURI ());
        writer.close ();

        Session session = Collap.getInstance ().getSessionFactory ().openSession ();
        Transaction trans = null;
        try {
            trans = session.beginTransaction ();
            User user = new User ("Marco");
            session.save (user);
            trans.commit ();
        }catch (HibernateException ex) {
            if (trans != null) {
                trans.rollback ();
            }
            ex.printStackTrace ();
        }finally {
            session.close ();
        }
    }

}
