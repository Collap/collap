package io.collap.std.post.category;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.post.entity.Category;
import io.collap.std.user.util.Permissions;
import io.collap.util.ParseUtils;
import org.hibernate.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditCategory extends TemplateController {

    public EditCategory (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
        Long id = ParseUtils.parseLong (remainingPath);
        Category category;

        if (id == null) {
            if (remainingPath.isEmpty ()) {
                category = Category.createTransientCategory ();
            }else {
                response.setStatus (HttpStatus.notFound);
                response.setStatusMessage ("Category not found!");
                return;
            }
        }else {
            category = (Category) session.get (Category.class, id);
            if (category == null) {
                response.getContentWriter ().write ("Category not found!");
            }
        }

        Map<String, Object> model = new HashMap<> ();
        model.put ("category", category);
        plugin.renderAndWriteTemplate ("category/Edit.jade", model, response.getContentWriter ());
        plugin.renderAndWriteTemplate ("category/Edit_head.jade", model, response.getHeadWriter ());
    }

    @Override
    protected void doPost (String remainingPath, Request request, Response response) throws IOException{
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
        Long id = request.getLongParameter ("id");
        if (id == null) {
            response.getContentWriter ().write ("ID parameter invalid.");
            return;
        }

        Category category;
        if (id.equals (-1L)) { /* Create new category. */
            category = new Category ();
        }else { /* Update category. */
            category = (Category) session.get (Category.class, id);
            if (category == null) {
                response.getContentWriter ().write ("Could not find category!");
                return;
            }
        }

        category.setName (request.getStringParameter ("name"));
        session.persist (category);

        response.getContentWriter ().write ("Updated category successfully.");
    }

}
