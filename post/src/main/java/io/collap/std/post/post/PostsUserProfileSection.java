package io.collap.std.post.post;

import io.collap.resource.TemplatePlugin;
import io.collap.std.post.entity.Post;
import io.collap.std.user.entity.User;
import io.collap.std.user.page.Profile;
import org.hibernate.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsUserProfileSection implements Profile.Section {

    private TemplatePlugin plugin;

    public PostsUserProfileSection (TemplatePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getSectionContent (User author) {
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
        String content = "";
        try {
            content = plugin.renderTemplate ("post/PostsUserProfileSection", model);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return content;
    }

    @Override
    public String getName () {
        return "Posts";
    }

}
