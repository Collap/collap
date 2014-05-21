package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.User;
import io.collap.std.user.UserPlugin;
import io.collap.std.user.util.Validator;
import io.collap.util.PasswordHash;
import io.collap.util.TransactionHelper;
import org.hibernate.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (type == Type.get) {
            showRegistrationForm (request, response);
        }else if (type == Type.post) {
            registerUser (request, response);
        }
    }

    private void showRegistrationForm (HttpServletRequest request, HttpServletResponse response) throws IOException {
        plugin.renderAndWriteTemplate ("Register", response.getWriter ());
    }

    private void registerUser (HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Only register user when no user is currently logged in.

        String username = request.getParameter ("username");
        String password = request.getParameter ("password");

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
        TransactionHelper transactionHelper = plugin.getCollap ().getTransactionHelper ();
        boolean success = transactionHelper.save (newUser);
        removeReservedUsernameFromList (username);

        if (success) {
            response.getWriter ().write ("User " + username + " created!");
        }else {
            registerError ("An unexpected error occurred while saving the newly created user object. Please try again.", response);
        }
    }

    /**
     * Registers the user name as reserved when returning false.
     */
    private boolean isUsernameReserved (String username) {
        String usernameLowercase = username.toLowerCase ();
        /* Note: This needs to be *one* sync block to exclude any possibility that two users with the same name can register at once.
         * So DO NOT split this up. */
        synchronized (reservedUsernames) {
            if (reservedUsernames.contains (usernameLowercase)) {
                return true;
            }

            Session session = plugin.getCollap ().getSessionFactory ().openSession ();
            User user = (User) session.createQuery ("from User as user where user.username = ?").setString (0, username).uniqueResult ();
            session.close ();
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

    private void registerError (String error, HttpServletResponse response) throws IOException {
        // TODO: Proper error response (Special field in user/Register.html)
        response.getWriter ().write (error);
    }

}
