package io.collap.std.post.cache;

import io.collap.plugin.Module;
import io.collap.std.post.entity.Post;

public class KeyUtils {

    public static String getViewPostKey (Module module, Post post) {
        return getViewPostKey (module.getName (), "" + post.getId ());
    }

    public static String getViewPostKey (String pluginName, String idString) {
        return pluginName + ":post.ViewPost:" + idString;
    }

}
