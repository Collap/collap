package io.collap.std.post.post;

import io.collap.controller.ModuleController;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.JadeDependant;
import io.collap.std.post.entity.Post;
import io.collap.template.TemplateRenderer;
import org.hibernate.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPosts extends ModuleController implements JadeDependant {

    private TemplateRenderer renderer;

    /**
     * Posts are sorted by timestamp.
     *
     * Possible GET parameters:
     *   - categories: A comma separated list of categories.
     *                 A post with either category is listed.
     */
    @Override
    public void doGet (Response response) throws IOException {
        String categoryString = request.getStringParameter ("categories");

        if (categoryString == null || categoryString.isEmpty ()) {
            response.getContentWriter ().write ("No categories included in the search!");
            return;
        }

        long time = System.nanoTime ();

        String[] categoryNames = categoryString.split (",");
        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        List<Post> posts = session
                .createQuery (
                                "select post " +
                                "from Post as post " +
                                "join post.categories as category " +
                                "where category.name in :names " +
                                "order by post.lastEdit desc"
                             )
                .setParameterList ("names", categoryNames)
                .setMaxResults (20)
                .list ();

        Map<String, Object> model = new HashMap<> ();
        model.put ("posts", posts);

        String queryTimeMessage = "Query time: " + (System.nanoTime () - time) + "ns<br>";
        time = System.nanoTime ();

        renderer.renderAndWriteTemplate ("post/List", model, response.getContentWriter ());
        renderer.renderAndWriteTemplate ("post/List_head", response.getHeadWriter ());

        response.getContentWriter ().write (queryTimeMessage);
        response.getContentWriter ().write ("Render time: " + (System.nanoTime () - time) + "ns");
    }

    @Override
    public void setRenderer (TemplateRenderer templateRenderer) {
        renderer = templateRenderer;
    }

}
