package io.collap.std.post.page;

import io.collap.controller.TemplateController;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.Post;
import io.collap.std.entity.User;
import io.collap.std.post.util.PostUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class View extends TemplateController {

    public View (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (type != Type.get) {
            return;
        }

        /* Get post. */
        Post post = PostUtil.getPostFromDatabase (plugin.getCollap (), remainingPath, false);
        if (post == null) {
            response.getWriter ().write ("Post not found!");
            return;
        }

        /* Get author. */
        User author = plugin.getCollap ().getTransactionHelper ().load (post.getAuthorId (), User.class);
        if (author == null) {
            response.getWriter ().write ("Author not found!");
            return; // TODO: When the author is null, use a "unknown" dummy object instead, so the post is still viewable.
        }

        /* Render template. */
        Map<String, Object> model = new HashMap<> ();
        model.put ("post", post);
        model.put ("author", author);
        model.put ("formattedPublishingDate", DateFormat.getDateInstance ().format (post.getPublishingDate ()));
        model.put ("formattedLastEdit", DateFormat.getDateInstance ().format (post.getLastEdit ()));
        plugin.renderAndWriteTemplate ("View", model, response.getWriter ());
    }

}
