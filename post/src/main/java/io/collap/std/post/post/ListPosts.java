package io.collap.std.post.post;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.post.entity.Post;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

public class ListPosts extends ModuleController implements BrygDependant {

    private Environment bryg;

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

        Model model = bryg.createModel ();
        model.setVariable ("posts", posts);

        String queryTimeMessage = "Query time: " + (System.nanoTime () - time) + "ns<br>";
        time = System.nanoTime ();

        bryg.getTemplate ("post.List").render (response.getContentWriter (), model);
        bryg.getTemplate ("post.List_head").render (response.getHeadWriter (), model);

        response.getContentWriter ().write (queryTimeMessage);
        response.getContentWriter ().write ("Render time: " + (System.nanoTime () - time) + "ns");
    }

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
