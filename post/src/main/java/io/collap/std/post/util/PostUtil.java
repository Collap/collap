package io.collap.std.post.util;

import io.collap.std.post.entity.Post;
import io.collap.util.ParseUtils;
import org.hibernate.Session;

public class PostUtil {

    public static Post getPostFromDatabaseOrCreate (Session session, String remainingPath, boolean create) {
        Long id = ParseUtils.parseLong (remainingPath);
        if (id == null) {
            id = -1L;
        }

        Post post = null;
        if (id.equals (-1L)) { /* Post not found. */
            if (create) {
                post = Post.createTransientPost ();
            }
        }else {
            post = (Post) session.get (Post.class, id);
        }

        return post;
    }

}
