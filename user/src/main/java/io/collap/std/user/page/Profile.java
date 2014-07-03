package io.collap.std.user.page;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.user.entity.User;
import org.hibernate.Session;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller shows the profile of a user. If the remainingPath is an integer, the user with the integer as the ID
 *  is displayed. Otherwise, if a user is logged in, their own profile is shown.
 */
public class Profile extends TemplateController {

    public interface Section {

        // TODO: The controller API is perfect for this.
        // TODO: Sorting could be done in a tiered alphabetic system (tier id before name).

        public String getSectionContent (User user);

        /**
         * Sections are sorted with the name in alphabetic order.
         */
        public String getName ();

    }

    private List<Section> sections;

    public Profile (TemplatePlugin plugin) {
        super (plugin);
        sections = new ArrayList<> ();
    }

    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        long id = -1;
        if (remainingPath.length () > 0) {
            try {
                id = Long.parseLong (remainingPath);
            } catch (NumberFormatException ex) {
                /* Expected. */
            }
        }

        if (id == -1) { /* Display logged in user. */
            HttpSession httpSession = request.getHttpRequest ().getSession ();
            if (httpSession != null) {
                User user = (User) httpSession.getAttribute ("user");
                if (user != null) {
                    displayUser (user, request, response);
                    return;
                }
            }
        }else { /* Fetch user from DB. */
            Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
            User user = (User) session.get (User.class, id);
            if (user != null) {
                displayUser (user, request, response);
                return;
            }
        }

        /* Can't display any user. */
        response.setStatus (HttpStatus.notFound);
    }

    @Override
    public boolean handleError (Request request, Response response) throws IOException {
        if (response.getStatus () == HttpStatus.notFound) {
            response.getContentWriter ().write ("User not found.");
            return true;
        }

        return false;
    }

    public class SectionContent {

        private String name;
        private String content;

        private SectionContent (String name, String content) {
            this.name = name;
            this.content = content;
        }

        public String getName () {
            return name;
        }

        public String getContent () {
            return content;
        }

    }

    public void displayUser (User user, Request request, Response response) throws IOException {
        List<SectionContent> sectionContents = new ArrayList<> ();
        for (Section section : sections) {
            sectionContents.add (new SectionContent (section.getName (), section.getSectionContent (user)));
        }

        Map<String, Object> model = new HashMap<> ();
        model.put ("user", user);
        model.put ("sections", sectionContents);
        plugin.renderAndWriteTemplate ("Profile", model, response.getContentWriter ());
        plugin.renderAndWriteTemplate ("Profile_head", model, response.getHeadWriter ());
    }

    public void addSection (Section section) {
        sections.add (section);
    }

}
