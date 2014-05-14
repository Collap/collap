package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.User;
import org.hibernate.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This controller shows the profile of a user. If the remainingPath is an integer, the user with the integer as the ID
 *  is displayed. Otherwise, if a user is logged in, their own profile is shown.
 */
public class Profile extends TemplateController {

    public Profile (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        long id = -1;
        if (remainingPath.length () > 0) {
            try {
                id = Long.parseLong (remainingPath);
            } catch (NumberFormatException ex) {
                /* Expected. */
            }
        }

        if (id == -1) { /* Display logged in user. */
            HttpSession httpSession = request.getSession ();
            if (httpSession != null) {
                User user = (User) httpSession.getAttribute ("user");
                if (user != null) {
                    displayUser (user, request, response);
                    return;
                }
            }
        }else { /* Fetch user from DB. */
            Session session = plugin.getCollap ().getSessionFactory ().openSession ();
            User user = (User) session.createQuery ("from User as user where user.id = ?").setLong (0, id).uniqueResult ();
            session.close ();
            if (user != null) {
                displayUser (user, request, response);
                return;
            }
        }

        /* Can't display any user. */
        response.getWriter ().write ("User not found.");
    }

    public void displayUser (User user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> model = new HashMap<> ();
        model.put ("user", user);
        plugin.renderAndWriteTemplate ("Profile", model, response.getWriter ());
    }

}
