package io.collap.std.post.util;

import io.collap.controller.communication.Request;
import io.collap.std.post.entity.Post;
import io.collap.std.user.entity.User;
import io.collap.util.ParseUtils;
import org.hibernate.Session;

public class PostUtil {

    // TODO: Find a solution with less duplicate code.

    public static Post getPostFromDatabase (Session session, String remainingPath) {
        return getPostFromDatabaseOrCreate (session, remainingPath, false);
    }

    public static Post getPostFromDatabaseOrCreate (Session session, String remainingPath, boolean create) {
        Long id = ParseUtils.parseLong (remainingPath);
        Post post;
        if (id == null) { /* Post not found. */
            if (create) {
                post = Post.createTransientPost ();
            }else {
                post = null;
            }
        }else {
            post = (Post) session.get (Post.class, id);
        }

        return post;
    }

    public static boolean isUserAuthor (Request request, Post post) {
        User user = (User) request.getHttpSession ().getAttribute ("user");
        return user != null && isUserAuthor (user, post);
    }

    public static boolean isUserAuthor (User user, Post post) {
        return user.getId ().equals (post.getAuthor ().getId ());
    }

}
