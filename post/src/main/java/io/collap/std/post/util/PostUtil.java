package io.collap.std.post.util;

import io.collap.Collap;
import io.collap.std.entity.Post;
import org.hibernate.Session;

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
            /* Fetch post from DB. */
            Session session = collap.getSessionFactory ().openSession ();
            post = (Post) session.createQuery ("from Post as post where post.id = ?").setLong (0, id).uniqueResult ();
            session.close ();
        }
        return post;
    }





}
