package io.collap.std.post.util;

import io.collap.Collap;
import io.collap.std.entity.Post;

public class PostUtil {

    public static Post getPostFromDatabase (Collap collap, String remainingPath, boolean create) {
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
            }
        }else {
            post = collap.getTransactionHelper ().load (id, Post.class);
        }

        return post;
    }

}
