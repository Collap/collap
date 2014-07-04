package io.collap.std.post.post;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.PostUtil;
import io.collap.std.user.util.Permissions;
import org.hibernate.Session;

import java.io.IOException;

public class DeletePost extends TemplateController {

    public DeletePost (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You need to be logged in!");
            return;
        }

        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();

        /* Get post. */
        Post post = PostUtil.getPostFromDatabase (session, remainingPath);
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
