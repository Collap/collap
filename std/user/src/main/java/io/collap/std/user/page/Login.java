package io.collap.std.user.page;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.BasicModel;
import io.collap.bryg.model.Model;
import io.collap.bryg.template.Template;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.user.entity.User;
import io.collap.util.PasswordHash;
import org.hibernate.Session;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class Login extends ModuleController implements BrygDependant {

    /* Dependencies. */
    private Environment bryg;

    private Template loginTemplate;
    private Template loginHeadTemplate;

    // TODO: Absolute paths for the login template (So it can be included into any page).
    //          Idea: Use a property that points to the path of the current controller.


    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

        loginTemplate = bryg.getTemplate ("Login");
        loginHeadTemplate = bryg.getTemplate ("Login_head");
    }

    @Override
    public void doGet (Response response) throws IOException {
        Model model = new BasicModel ();
        loginTemplate.render (response.getContentWriter (), model);
        loginHeadTemplate.render (response.getHeadWriter (), model);
    }

    @Override
    public void doPost (Response response) throws IOException {
        Model model = new BasicModel ();
        boolean success = processLogin (request, model);
        if (!success) {
            loginTemplate.render (response.getContentWriter (), model);
            loginHeadTemplate.render (response.getHeadWriter (), model);
        }else {
            // TODO: Route to whatever page.
            response.getHeadWriter ().write ("<title>Login</title>");
            response.getContentWriter ().write ("Login successful!");
        }
    }

    /**
     * @param model A model which saves error messages from the login process in the variable <i>errors</i>.
     * @return Whether the login process was completed successfully and the user is logged in now.
     */
    private boolean processLogin (Request request, Model model) {
        // TODO: Cover the situation that the user is already logged in.

        String name = request.getStringParameter ("username");
        String password = request.getStringParameter ("password");

        List<String> errors = new ArrayList<> ();
        model.setVariable ("errors", errors);

        /* Fetch user from the DB. */
        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        User user = (User) session.createQuery ("from User as user where user.username = ?").setString (0, name).uniqueResult ();
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

        /* Set session attribute. */
        request.setSessionAttribute ("user", user);
        return true;
    }

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
