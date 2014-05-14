package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.User;
import io.collap.util.PasswordHash;
import org.hibernate.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends TemplateController {

    // TODO: Absolute paths for the login template (So it can be included into any page).
    //          Idea: Use a property that points to the path of the current controller.

    public Login (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    public void execute (Type type, String s, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (type == Type.get) {
            plugin.renderAndWriteTemplate ("Login", response.getWriter ());
        }else if (type == Type.post) {
            Map<String, Object> model = new HashMap<> ();
            boolean success = processLogin (request, response, model);
            if (!success) {
                plugin.renderAndWriteTemplate ("Login", model, response.getWriter ());
            }else {
                // TODO: Route to whatever page.
                response.getWriter ().write ("Login successful!");
            }
        }
    }

    /**
     * @param model A model which saves error message from the login process.
     * @return Whether the login process was completed successfully and the user is logged in now.
     */
    private boolean processLogin (HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
        // TODO: Cover the situation that the user is already logged in.

        String name = request.getParameter ("username");
        String password = request.getParameter ("password");

        List<String> errors = new ArrayList<> ();
        model.put ("errors", errors);

        /* Fetch user from the DB. */
        Session session = plugin.getCollap ().getSessionFactory ().openSession ();
        User user = (User) session.createQuery ("from User as user where user.username = ?").setString (0, name).uniqueResult ();
        session.close ();
        if (user == null) {
            errors.add ("Invalid username or password!");
            return false;
        }

        /* Compare passwords. */
        boolean correct = false;
        try {
            correct = PasswordHash.validatePassword (password, user.getPasswordHash ());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace ();
        }

        if (!correct) {
            errors.add ("Invalid username or password!");
            return false;
        }

        /* Set session. */
        HttpSession httpSession = request.getSession (true);
        httpSession.setAttribute ("user", user);
        return true;
    }

}
