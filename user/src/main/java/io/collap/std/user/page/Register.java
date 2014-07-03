package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.user.entity.User;
import io.collap.std.user.UserPlugin;
import io.collap.std.user.util.Validator;
import io.collap.util.PasswordHash;
import org.hibernate.Session;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Register extends TemplateController {

    /**
     * This list contains all user names that are currently reserved for registration.
     * This ensures that two users that are registering together in a very short time frame can not share a name.
     * TODO: The feature is currently UNTESTED.
     */
    private List<String> reservedUsernames = new ArrayList<> ();

    public Register (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        plugin.renderAndWriteTemplate ("Register", response.getContentWriter ());
        plugin.renderAndWriteTemplate ("Register_head", response.getHeadWriter ());
    }

    @Override
    protected void doPost (String remainingPath, Request request, Response response) throws IOException {
        registerUser (request, response);
    }

    private void registerUser (Request request, Response response) throws IOException {
        // TODO: Only register user when no user is currently logged in.

        String username = request.getHttpRequest ().getParameter ("username");
        String password = request.getHttpRequest ().getParameter ("password");

        Validator.ValidationResult userNameValidation = ((UserPlugin) plugin).getValidator ().validateUserName (username);
        if (!userNameValidation.passed) {
            registerError (userNameValidation.error, response);
            return;
        }

        final int minimumPasswordLength = 1;
        if (password.length () < minimumPasswordLength) { // TODO: Minimum threshold in config
            registerError ("The password must at least be " + minimumPasswordLength + " characters long!", response);
            return;
        }

        /* Check if the requested name is already taken. */
        boolean reserved = isUsernameReserved (username);
        if (reserved) {
            registerError ("User " + username + " already exists!", response);
            return;
        }

        User newUser = new User (username);
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
            removeReservedUsernameFromList (username);
            return;
        }

        /* Commit new user to the database. */
        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
        session.persist (newUser);
        removeReservedUsernameFromList (username);
        response.getContentWriter ().write ("User " + username + " created!");
    }

    /**
     * Registers the user name as reserved when returning false.
     * When this method returns <i>true</i>, the user name is <b>not</b> reserved!
     */
    private boolean isUsernameReserved (String username) {
        String usernameLowercase = username.toLowerCase ();
        /* Note: This needs to be *one* sync block to exclude any possibility that two users with the same name can register at once.
         * So DO NOT split this up. */
        synchronized (reservedUsernames) {
            if (reservedUsernames.contains (usernameLowercase)) {
                return true;
            }

            Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
            User user = (User) session
                    .createQuery ("from User as user where user.username = :username")
                    .setString ("username", username).uniqueResult ();
            if (user != null) {
                return true;
            }

            reservedUsernames.add (username.toLowerCase ());
        }

        return false;
    }

    private void removeReservedUsernameFromList (String username) {
        synchronized (reservedUsernames) {
            reservedUsernames.remove (username.toLowerCase ());
        }
    }

    private void registerError (String error, Response response) throws IOException {
        // TODO: Proper error response (Special field in user/Register.html)
        response.getContentWriter ().write (error);
    }

}
