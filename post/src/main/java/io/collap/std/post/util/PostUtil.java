package io.collap.std.post.util;

import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import org.hibernate.Session;

import java.util.HashSet;

public class PostUtil {

    public static Post getPostFromDatabaseOrCreate (Session session, String remainingPath, boolean create) {
        long id = -1;
        try {
            id = Long.parseLong (remainingPath);
        } catch (NumberFormatException ex) {
            /* Expected. */
        }

        Post post = null;
        if (id == -1) { /* Post not found. */
            if (create) {
                post = new Post ();
                post.setId (-1L);
                post.setCategories (new HashSet<Category> ());
            }
        }else {
            post = (Post) session.load (Post.class, id);
        }

        return post;
    }

}
