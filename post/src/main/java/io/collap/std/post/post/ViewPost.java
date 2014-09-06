package io.collap.std.post.post;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.cache.Cached;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.post.cache.KeyUtils;
import io.collap.std.post.entity.Post;
import io.collap.std.post.util.PostUtil;
import org.hibernate.Session;

import java.io.IOException;

public class ViewPost extends ModuleController implements BrygDependant, Cached {

    private Post post;
    private Environment bryg;

    @Override
    public void initialize (Request request, String remainingPath) {
        super.initialize (request, remainingPath);

        /* Get post. */
        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        post = PostUtil.getPostFromDatabase (session, remainingPath);
    }

    @Override
    public void doGet (Response response) throws IOException {
        if (post == null) {
            response.getContentWriter ().write ("Post not found!");
            return;
        }

        /* Render template. */
        Model model = bryg.createModel ();
        model.setVariable ("post", post);
        // model.put ("formattedPublishingDate", DateFormat.getDateInstance ().format (post.getPublishingDate ()));
        // model.put ("formattedLastEdit", DateFormat.getDateInstance ().format (post.getLastEdit ()));
        model.setVariable ("allowEdit", PostUtil.isUserAuthor (request, post));
        bryg.getTemplate ("post.View_head").render (response.getHeadWriter (), model);
        bryg.getTemplate ("post.View").render (response.getContentWriter (), model);
    }

    @Override
    public boolean shouldResponseBeCached () {
        return request.getMethod () == Request.Method.get
                && !PostUtil.isUserAuthor (request, post); /* Don't cache the author interface! */
    }

    @Override
    public String getElementKey () {
        return KeyUtils.getViewPostKey (module.getName (), post.getId ().toString ());
    }

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
