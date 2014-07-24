package io.collap.std.user.page;

import io.collap.controller.Controller;
import io.collap.controller.SectionController;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.InternalRequest;
import io.collap.controller.communication.Response;
import io.collap.std.user.entity.User;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller shows the profile of a user. If the remainingPath is an integer, the user with the integer as the ID
 *  is displayed. Otherwise, if a user is logged in, their own profile is shown.
 */
public class Profile extends SectionController {

    /* Init. */
    private long userId = -1;

    /* Execute. */
    private User user;

    @Override
    protected void configureSectionRequest (InternalRequest request) {
        request.setParameter ("user", user);
    }

    @Override
    public void initialize (String remainingPath) {
        if (remainingPath.length () > 0) {
            try {
                userId = Long.parseLong (remainingPath);
            } catch (NumberFormatException ex) {
                /* Expected. */
            }
        }
    }

    @Override
    public void doGet (Response response) throws IOException {
        if (userId == -1) { /* Display logged in user. */
            user = (User) request.getSessionAttribute ("user");
        }else { /* Fetch user from DB. */
            Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
            user = (User) session.get (User.class, userId);
        }

        if (user != null) {
            displayUser (user, response);
        }else {
            /* Can't display any user. */
            response.setStatus (HttpStatus.notFound);
        }
    }

    @Override
    public boolean handleError (Response response) throws IOException {
        if (response.getStatus () == HttpStatus.notFound) {
            response.getContentWriter ().write ("User not found.");
            return true;
        }

        return false;
    }

    public void displayUser (User user, Response response) throws IOException {
        List<Response> responses = new ArrayList<> ();
        for (Controller section : sections) {
            responses.add (executeSection (section));
        }

        Map<String, Object> model = new HashMap<> ();
        model.put ("user", user);
        model.put ("sections", responses);
        renderer.renderAndWriteTemplate ("Profile", model, response.getContentWriter ());
        renderer.renderAndWriteTemplate ("Profile_head", model, response.getHeadWriter ());
    }

}
