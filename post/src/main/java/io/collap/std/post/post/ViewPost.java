package io.collap.std.post.post;

import io.collap.cache.Cached;
import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.PostUtil;
import org.hibernate.Session;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class ViewPost extends TemplateController implements Cached {

    public ViewPost (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();

        /* Get post. */
        Post post = PostUtil.getPostFromDatabase (session, remainingPath);
        if (post == null) {
            response.getContentWriter ().write ("Post not found!");
            return;
        }

        /* Render template. */
        Map<String, Object> model = new HashMap<> ();
        model.put ("post", post);
        model.put ("formattedPublishingDate", DateFormat.getDateInstance ().format (post.getPublishingDate ()));
        model.put ("formattedLastEdit", DateFormat.getDateInstance ().format (post.getLastEdit ()));
        model.put ("viewerHasEditingPermissions", PostUtil.isUserAuthor (request, post));
        plugin.renderAndWriteTemplate ("post/View_head", model, response.getHeadWriter ());
        plugin.renderAndWriteTemplate ("post/View", model, response.getContentWriter ());
    }

    @Override
    public boolean isRequestMethodCached (Request.Method method) {
        return method == Request.Method.get;
    }

    @Override
    public String getElementKey (String remainingPath, Request request) {
        return plugin.getName () + ":post.ViewPost:" + remainingPath;
    }

}
