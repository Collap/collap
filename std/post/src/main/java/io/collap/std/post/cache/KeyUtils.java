package io.collap.std.post.cache;

import io.collap.std.post.PostModule;
import io.collap.std.post.entity.Post;

public class KeyUtils {

    public static String viewPost (Post post) {
        return viewPost (post.getId ().toString ());
    }

    public static String viewPost (Long id) {
        return viewPost (id.toString ());
    }

    public static String viewPost (String idString) {
        return PostModule.ARTIFACT_NAME + ":post.ViewPost:" + idString;
    }

}
