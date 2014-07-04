package io.collap.std.post.post;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;
import io.collap.std.post.entity.Post;
import org.hibernate.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPosts extends TemplateController {

    public ListPosts (TemplatePlugin plugin) {
        super (plugin);
    }

    /**
     * Posts are sorted by timestamp.
     *
     * Possible GET parameters:
     *   - categories: A comma separated list of categories.
     *                 A post with either category is listed.
     *
     * @param remainingPath Empty.
     */
    @Override
    protected void doGet (String remainingPath, Request request, Response response) throws IOException {
        String categoryString = request.getStringParameter ("categories");

        if (categoryString == null || categoryString.isEmpty ()) {
            response.getContentWriter ().write ("No categories included in the search!");
            return;
        }

        long time = System.nanoTime ();

        String[] categoryNames = categoryString.split (",");
        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
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

        plugin.renderAndWriteTemplate ("post/List", model, response.getContentWriter ());
        plugin.renderAndWriteTemplate ("post/List_head", response.getHeadWriter ());

        response.getContentWriter ().write (queryTimeMessage);
        response.getContentWriter ().write ("Render time: " + (System.nanoTime () - time) + "ns");
    }

}
