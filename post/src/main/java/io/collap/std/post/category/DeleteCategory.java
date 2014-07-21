package io.collap.std.post.category;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Response;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.CategoryUtil;
import io.collap.std.user.util.Permissions;
import org.hibernate.Session;

import java.io.IOException;
import java.util.Set;

public class DeleteCategory extends TemplateController {

    private String idString;

    @Override
    public void initialize (String remainingPath) {
        idString = remainingPath;
    }

    @Override
    public void doGet (Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
        Category category = CategoryUtil.getCategoryFromDatabase (session, idString);
        if (category == null) {
            response.getContentWriter ().write ("Category not found!");
            return;
        }

        /* Remove the category from all posts! */
        Set<Post> posts = category.getPosts ();
        for (Post post : posts) {
            post.getCategories ().remove (category);
            session.persist (post); /* Required for post cache invalidation! */
        }

        session.delete (category);
        response.getContentWriter ().write ("Category successfully deleted!");
    }

}
