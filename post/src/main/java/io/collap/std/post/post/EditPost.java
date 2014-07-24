package io.collap.std.post.post;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.std.markdown.MarkdownModule;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import io.collap.std.user.entity.User;
import io.collap.std.post.util.PostUtil;
import io.collap.std.user.util.Permissions;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class EditPost extends TemplateController {

    private String idString;

    @Override
    public void initialize (String remainingPath) {
        idString = remainingPath;
    }

    /**
     * Either write a new post or edit a post that already exists.
     * The remainingPath determines whether a new post is created: When an ID is supplied, the post with that ID is edited.
     * Otherwise, a new post is created.
     */
    @Override
    public void doGet (Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        Post post = PostUtil.getPostFromDatabaseOrCreate (session, idString, true);
        if (post != null) {
            User user = (User) request.getSessionAttribute ("user");
            if (post.getId () == -1 || PostUtil.isUserAuthor (user, post)) {
                Map<String, Object> model = new HashMap<> ();
                model.put ("post", post);
                model.put ("categories", post.getCategories ());
                // TODO: The following solution is temporary.
                String categoryString = "";
                for (Category category : post.getCategories ()) {
                    if (!categoryString.isEmpty ()) {
                        categoryString += ",";
                    }
                    categoryString += category.getName ();
                }
                model.put ("categoryString", categoryString);
                renderer.renderAndWriteTemplate ("post/Edit_head", model, response.getHeadWriter ());
                renderer.renderAndWriteTemplate ("post/Edit", model, response.getContentWriter ());
            }else {
                response.getContentWriter ().write ("Insufficient editing permissions!");
            }
        }else {
            // TODO: Potential source of knowledge for an outsider of which IDs are taken.
            response.getContentWriter ().write ("Post not found!");
        }
    }

    /**
     * Update or add a post.
     */
    @Override
    public void doPost (Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        // TODO: Possible validation.
        Long id = request.getLongParameter ("id");
        if (id == null) {
            response.getContentWriter ().write ("Hidden 'id' input field supplied a wrong number!");
            return;
        }

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();

        /* Note: It is assumed that a check whether a user is logged in already passed. */
        User author = (User) request.getSessionAttribute ("user");

        Date now = new Date ();
        Post post;
        if (id.equals (-1L)) { /* Create new post! */
            post = new Post ();
            post.setCategories (new HashSet<Category> ());
            post.setAuthor (author);
            post.setPublishingDate (now);
        }else {
            post = (Post) session.get (Post.class, id);
            if (post == null) {
                response.getContentWriter ().write ("Post could not be found!");
                return;
            }

            /* Validate author. */
            if (!PostUtil.isUserAuthor (author, post)) {
                response.getContentWriter ().write ("Insufficient rights to edit the post!");
                return;
            }
        }

        // TODO: Escape the HTML in the title field.
        post.setTitle (request.getStringParameter ("title"));
        post.setContent (request.getStringParameter ("content"));
        post.setLastEdit (now);

        MarkdownModule markdownModule = (MarkdownModule) module.getCollap ().getPluginManager ().getPlugins ().get ("std-markdown");
        post.setCompiledContent (markdownModule.convertMarkdownToHTML (post.getContent ()));

        /* Update categories. */
        updateCategories (post, request, response);

        /* Update post. */
        session.persist (post);
        response.getContentWriter ().write ("Post successfully created or updated!");
    }

    private void updateCategories (Post post, Request request, Response response) throws IOException {
        String[] inputNames = request.getStringParameter ("categories").split (",");
        Set<Category> categories = post.getCategories ();

        /* Trim category names. */
        for (int i = 0; i < inputNames.length; ++i) {
            inputNames[i] = inputNames[i].trim ();
        }

        /* Remove unreferenced categories. */
        Iterator<Category> iterator = categories.iterator ();
        while (iterator.hasNext ()) {
            Category category = iterator.next ();
            boolean contained = false;
            for (String name : inputNames) {
                if (name.isEmpty ()) continue;
                if (category.getName ().equals (name)) {
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                iterator.remove ();
            }
        }

        /* Find all categories that are not referenced yet to minimize database load. */
        List<String> newNames = new ArrayList<> ();
        for (String name : inputNames) {
            if (name.isEmpty ()) continue;

            boolean contained = false;
            for (Category category : categories) {
                if (category.getName ().equals (name)) {
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                newNames.add (name);
            }
        }

        /* Get all newly referenced categories from the database. */
        if (newNames.size () > 0) {
            String query = "from Category as category where category.name in :names";
            List<Category> newCategories = module.getCollap ().getSessionFactory ().getCurrentSession ()
                    .createQuery (query)
                    .setParameterList ("names", newNames)
                    .list ();

            categories.addAll (newCategories);

            for (Category category : newCategories) { /* After this operation all invalid names will be left. */
                newNames.remove (category.getName ());
            }

            /* Display which categories could not be found. */
            if (newNames.size () > 0) {
                Writer writer = response.getContentWriter ();
                writer.write ("The following categories could not be found and were not added to the post: <br />");
                for (String categoryName : newNames) {
                    writer.write (categoryName + "<br />");
                }
                writer.write ("<br />");
            }
        }
    }

}
