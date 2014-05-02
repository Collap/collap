package io.collap.std.user.page;

import io.collap.Collap;
import io.collap.controller.Controller;
import io.collap.std.entity.User;
import io.collap.std.user.UserPlugin;
import io.collap.util.PasswordHash;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Register implements Controller {

    private UserPlugin plugin;

    /**
     * This list contains all user names that are currently reserved for registration.
     * This ensures that two users that are registering together in a very short time frame can not share a name.
     * TODO: The feature is currently UNTESTED.
     */
    private List<String> reservedUserNames = new ArrayList<String> ();

    public Register (UserPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (type == Type.get) {
            showRegistrationForm (request, response);
        }else if (type == Type.post) {
            registerUser (request, response);
        }
    }

    private void showRegistrationForm (HttpServletRequest request, HttpServletResponse response) throws IOException {
        WebContext context = new WebContext (request, response, request.getServletContext (), request.getLocale ());
        Collap.getInstance ().getTemplateEngine ().process (plugin.getName () + "/template/user/Register", context, response.getWriter ());
    }

    private void registerUser (HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter ("name");
        String password = request.getParameter ("password");

        final int minimumNameLength = 1;
        if (name.length () < minimumNameLength) { // TODO: Minimum threshold in config
            registerError ("The name must at least be " + minimumNameLength + " characters long!", response);
            return;
        }

        final int minimumPasswordLength = 1;
        if (password.length () < minimumPasswordLength) { // TODO: Minimum threshold in config
            registerError ("The password must at least be " + minimumPasswordLength + " characters long!", response);
            return;
        }

        /* Check if the requested name is already taken. */
        // TODO: Figure out how not to give out valid user names this way (Probably via email).
        boolean reserved = isUserNameReserved (name);
        if (reserved) {
            registerError ("User " + name + " already exists!", response);
            return;
        }

        User newUser = new User (name);
        // TODO: Handle exceptions properly.
        try {
            newUser.setPasswordHash (PasswordHash.createHash (password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace ();
        }

        /* Catch problems with the generated password hash. */
        if (newUser.getPasswordHash ().length () <= 0) {
            registerError ("An unexpected error occurred. Please try again.", response);
            removeReservedUserNameFromList (name);
            return;
        }

        /* Commit new user to the database. */
        boolean success = true;
        {
            Session session = Collap.getInstance ().getSessionFactory ().openSession ();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction ();
                session.save (newUser);
                transaction.commit ();
            } catch (HibernateException ex) {
                if (transaction != null) {
                    transaction.rollback ();
                }
                registerError ("An unexpected error occurred while saving the newly created user object. Please try again.", response);
                success = false;
            } finally {
                session.close ();
            }
        }

        removeReservedUserNameFromList (name);

        if (success) {
            response.getWriter ().write ("User " + name + " created!");
        }
    }

    /**
     * Registers the user name as reserved when returning false.
     */
    private boolean isUserNameReserved (String name) {
        String nameLowercase = name.toLowerCase ();
        /* Note: This needs to be *one* sync block to exclude any possibility that two users with the same name can register at once.
         * So DO NOT split this up. */
        synchronized (reservedUserNames) {
            if (reservedUserNames.contains (nameLowercase)) {
                return true;
            }

            Session session = Collap.getInstance ().getSessionFactory ().openSession ();
            User user = (User) session.createQuery ("from User as user where user.name = ?").setString (0, name).uniqueResult ();
            session.close ();
            if (user != null) {
                return true;
            }

            reservedUserNames.add (name.toLowerCase ());
        }

        return false;
    }

    private void removeReservedUserNameFromList (String name) {
        synchronized (reservedUserNames) {
            reservedUserNames.remove (name.toLowerCase ());
        }
    }

    private void registerError (String error, HttpServletResponse response) throws IOException {
        // TODO: Proper error response (Special field in user/Register.html)
        response.getWriter ().write (error);
    }

}
