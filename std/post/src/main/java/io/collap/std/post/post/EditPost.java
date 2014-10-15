package io.collap.std.post.post;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.post.PostModule;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import io.collap.std.post.type.Type;
import io.collap.std.user.entity.User;
import io.collap.std.post.util.PostUtil;
import io.collap.std.user.util.Permissions;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class EditPost extends ModuleController implements BrygDependant {

    private Environment bryg;
    private String idString;

    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

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
                Map<String, Type> types = ((PostModule) module).getPostTypes ();

                /* Check if type name exists, if not display selection. */
                String typeName = post.getTypeName ();
                if (typeName == null) {
                    typeName = request.getStringParameter ("type");
                    if (typeName == null) {
                        /* Display a type selection first! */
                        Model model = bryg.createModel ();
                        model.setVariable ("types", types.keySet ());
                        bryg.getTemplate ("post.SpecifyType").render (response.getContentWriter (), model);
                        return;
                    }else {
                        post.setTypeName (typeName);
                    }
                }

                if (!types.containsKey (typeName)) {
                    response.getContentWriter ().write ("The post type " + typeName + " does not exist!");
                    return;
                }

                Model model = bryg.createModel ();
                model.setVariable ("post", post);
                Type type = types.get (post.getTypeName ());
                model.setVariable ("customEditorSource", type.getEditor (post.getTypeDataId ()));
                bryg.getTemplate ("post.Edit_head").render (response.getHeadWriter (), model);
                bryg.getTemplate ("post.Edit").render (response.getContentWriter (), model);
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
            post.setTypeDataId (null);
            post.setTypeName (request.getStringParameter ("typeName"));
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

        post.setLastEdit (now);

        Type type = ((PostModule) module).getPostTypes ().get (post.getTypeName ());
        post.setTypeDataId (type.update (post.getTypeDataId (), request));
        type.compile (post);

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

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
