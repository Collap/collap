package io.collap.std.post.post;

import io.collap.cache.Cached;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.JadeDependant;
import io.collap.std.post.cache.KeyUtils;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.PostUtil;
import io.collap.template.TemplateRenderer;
import org.hibernate.Session;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

public class ViewPost extends ModuleController implements JadeDependant, Cached {

    private String idString;
    private TemplateRenderer renderer;

    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

        idString = remainingPath;
    }

    @Override
    public void doGet (Response response) throws IOException {
        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();

        /* Get post. */
        Post post = PostUtil.getPostFromDatabase (session, idString);
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
        renderer.renderAndWriteTemplate ("post/View_head", model, response.getHeadWriter ());
        renderer.renderAndWriteTemplate ("post/View", model, response.getContentWriter ());
    }

    @Override
    public boolean shouldResponseBeCached () {
        return request.getMethod () == Request.Method.get;
    }

    @Override
    public String getElementKey () {
        return KeyUtils.getViewPostKey (module.getName (), idString);
    }

    @Override
    public void setRenderer (TemplateRenderer templateRenderer) {
        renderer = templateRenderer;
    }

}
