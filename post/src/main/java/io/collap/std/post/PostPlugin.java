package io.collap.std.post;

import io.collap.controller.Dispatcher;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.Post;
import io.collap.std.entity.User;
import io.collap.std.post.page.Edit;
import io.collap.std.post.page.View;
import io.collap.std.user.UserPlugin;
import io.collap.std.user.page.Profile;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.*;

public class PostPlugin extends TemplatePlugin {

    @Override
    public void initialize () {
        super.initialize ();

        Dispatcher postDispatcher = new Dispatcher ();
        Edit editController = new Edit (this);
        postDispatcher.registerController ("edit", editController);
        postDispatcher.registerController ("new", editController);
        postDispatcher.registerController ("view", new View (this));
        collap.getRootDispatcher ().registerController ("post", postDispatcher);

        /* Add the posts section to the profile page! */
        UserPlugin userPlugin = (UserPlugin) collap.getPluginManager ().getPlugins ().get ("std-user");
        Profile profilePage = userPlugin.getProfilePage ();
        if (profilePage != null) {
            profilePage.addSection (new Profile.Section () {
                @Override
                public String getSectionContent (User author) {
                    Session session = collap.getSessionFactory ().openSession ();
                    List<Object[]> rows = session
                            .createQuery ("select id, title from Post as post where post.authorId = ?")
                            .setLong (0, author.getId ())
                            .list ();
                    session.close ();

                    List<Post> posts = new ArrayList<Post> ();
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
                        content = PostPlugin.this.renderTemplate ("ProfileSection/Posts", model);
                    } catch (IOException e) {
                        e.printStackTrace ();
                    }
                    return content;
                }

                @Override
                public String getName () {
                    return "Posts";
                }
            });
        }
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (Post.class);
    }

}
