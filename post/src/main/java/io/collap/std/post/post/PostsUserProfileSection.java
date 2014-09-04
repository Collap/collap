package io.collap.std.post.post;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.controller.ModuleController;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygDependant;
import io.collap.std.post.entity.Post;
import io.collap.std.user.entity.User;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostsUserProfileSection extends ModuleController implements BrygDependant {

    private Environment bryg;

    @Override
    public void doGet (Response response) throws IOException {
        User author = (User) request.getParameter ("user");

        Session session = module.getCollap ().getSessionFactory ().getCurrentSession ();
        List<Object[]> rows = session
                .createQuery ("select id, title from Post as post where post.author.id = :authorId")
                .setLong ("authorId", author.getId ())
                .list ();

        List<Post> posts = new ArrayList<> ();
        for (Object[] row : rows) {
            Post post = new Post ();
            post.setId ((Long) row[0]);
            post.setTitle ((String) row[1]);
            posts.add (post);
        }

        Model model = bryg.createModel ();
        model.setVariable ("posts", posts);
        bryg.getTemplate ("section.user.Posts").render (response.getContentWriter (), model);
        response.getHeadWriter ().write ("Posts");
    }

    @Override
    public void setBryg (Environment environment) {
        bryg = environment;
    }

}
