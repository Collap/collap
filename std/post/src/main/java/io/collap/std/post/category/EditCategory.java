package io.collap.std.post.category;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.BasicModel;
import io.collap.bryg.model.Model;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.HttpStatus;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.post.entity.Category;
import io.collap.std.user.util.Permissions;
import io.collap.util.ParseUtils;
import org.hibernate.Session;

import java.io.IOException;

public class EditCategory extends ModuleController implements BrygDependant {

    private String idString;
    private Environment bryg;

    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

        idString = remainingPath;
    }

    @Override
    public void doGet (Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        Long id = ParseUtils.parseLong (idString);
        Category category;

        if (id == null) {
            if (idString.isEmpty ()) {
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
                return;
            }
        }

        Model model = new BasicModel ();
        model.setVariable ("category", category);
        bryg.getTemplate ("category.Edit"). render (response.getContentWriter (), model);
        bryg.getTemplate ("category.Edit_head").render (response.getHeadWriter (), model);
    }

    @Override
    public void doPost (Response response) throws IOException{
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
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

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
