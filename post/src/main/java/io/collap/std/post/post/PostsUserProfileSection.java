package io.collap.std.post.post;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Response;
import io.collap.std.post.entity.Post;
import io.collap.std.user.entity.User;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsUserProfileSection extends TemplateController {

    @Override
    public void initialize (String remainingPath) {

    }

    @Override
    public void doGet (Response response) throws IOException {
        User author = (User) request.getParameter ("user");

        Session session = plugin.getCollap ().getSessionFactory ().getCurrentSession ();
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

        Map<String, Object> model = new HashMap<> ();
        model.put ("posts", posts);
        renderer.renderAndWriteTemplate ("post/PostsUserProfileSection", model, response.getContentWriter ());
        response.getHeadWriter ().write ("Posts");
    }

}
