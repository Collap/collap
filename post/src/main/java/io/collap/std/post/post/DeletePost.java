package io.collap.std.post.post;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Response;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.PostUtil;
import io.collap.std.user.util.Permissions;
import org.hibernate.Session;

import java.io.IOException;

public class DeletePost extends TemplateController {

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

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();

        /* Get post. */
        Post post = PostUtil.getPostFromDatabase (session, idString);
        if (post == null) {
            response.getContentWriter ().write ("Post not found!");
            return;
        }

        /* Check permissions. */
        if (!PostUtil.isUserAuthor (request, post)) {
            response.getContentWriter ().write ("Insufficient permissions to delete the post!");
            return;
        }

        /* Delete post. */
        session.delete (post);
        response.getContentWriter ().write ("Deleted post successfully!");
    }

}
