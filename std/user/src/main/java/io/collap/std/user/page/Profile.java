package io.collap.std.user.page;

import io.collap.bryg.Template;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.cache.Cached;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.InternalRequest;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.controller.provider.SectionDependant;
import io.collap.controller.section.Section;
import io.collap.std.user.cache.KeyUtil;
import io.collap.std.user.entity.User;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This controller shows the profile of a user. If the remainingPath is an integer, the user with the integer as the ID
 *  is displayed. Otherwise, if a user is logged in, their own profile is shown.
 */
public class Profile extends ModuleController implements Cached, SectionDependant, BrygDependant {

    /* Dependencies. */
    private List<Section> sections;
    private Environment bryg;

    /* Init. */
    private long userId = -1;

    /* Execute. */
    private User user;

    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

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
        for (Section section : sections) {
            responses.add (section.execute (request));
        }

        Model model = bryg.createModel ();
        model.setVariable ("user", user);
        model.setVariable ("sections", responses);
        Template profile = bryg.getTemplate ("Profile");
        profile.render (response.getContentWriter (), model);
        Template profileHead = bryg.getTemplate ("Profile_head");
        profileHead.render (response.getHeadWriter (), model);
    }

    @Override
    public void configureSectionRequest (InternalRequest request) {
        request.setParameter ("user", user);
    }

    @Override
    public void setSections (List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

    @Override
    public boolean shouldResponseBeCached () {
        return request.getMethod () == Request.Method.get
                && userId >= 0;
    }

    @Override
    public String getElementKey () {
        return KeyUtil.userProfile (userId);
    }

}
