package io.collap.std.post.cache;

import io.collap.resource.Plugin;
import io.collap.std.post.entity.Post;

public class KeyUtils {

    public static String getViewPostKey (Plugin plugin, Post post) {
        return getViewPostKey (plugin.getName (), "" + post.getId ());
    }

    public static String getViewPostKey (String pluginName, String idString) {
        return pluginName + ":post.ViewPost:" +idString;
    }

}
