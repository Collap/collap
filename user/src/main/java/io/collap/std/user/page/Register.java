package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.std.user.entity.User;
import io.collap.std.user.UserPlugin;
import io.collap.std.user.util.Validator;
import io.collap.util.PasswordHash;
import org.hibernate.Session;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Register extends TemplateController {

    @Override
    public void initialize (String s) {

    }

    @Override
    public void doGet (Response response) throws IOException {
        renderer.renderAndWriteTemplate ("Register", response.getContentWriter ());
        renderer.renderAndWriteTemplate ("Register_head", response.getHeadWriter ());
    }

    @Override
    public void doPost (Response response) throws IOException {
        registerUser (request, response);
    }

    private void registerUser (Request request, Response response) throws IOException {
        // TODO: Only register user when no user is currently logged in.

        String username = request.getStringParameter ("username");
        String password = request.getStringParameter ("password");

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
        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
        Long count = (Long) session
                .createQuery ("select count(user) from User as user where user.username = :username")
                .setString ("username", username)
                .uniqueResult ();
        if (count > 0) {
            registerError ("User " + username + " already exists!", response);
            return;
        }

        User newUser = new User (username);
        // TODO: Handle exceptions properly.
        try {
            newUser.setPasswordHash (PasswordHash.createHash (password));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace ();
        }

        /* Catch problems with the generated password hash. */
        if (newUser.getPasswordHash ().length () <= 0) {
            registerError ("An unexpected error occurred. Please try again.", response);
            return;
        }

        /* Commit new user to the database. */
        session.persist (newUser);
        response.getContentWriter ().write ("User " + username + " created!");
    }

    private void registerError (String error, Response response) throws IOException {
        // TODO: Proper error response (Special field in user/Register.html)
        response.getContentWriter ().write (error);
    }

}
