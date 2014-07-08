package io.collap.std.post.cache;

import io.collap.cache.BasicInvalidator;
import io.collap.resource.Plugin;
import io.collap.std.post.entity.Post;
import net.sf.ehcache.Cache;

public class PostInvalidator extends BasicInvalidator {

    public PostInvalidator (Plugin plugin) {
        super (plugin);
    }

    @Override
    public void invalidate (Object entity) {
        Post post = (Post) entity;

        /* Invalidate ViewPost pages. */
        Cache fragmentCache = plugin.getCollap ().getFragmentCache ();
        fragmentCache.remove (KeyUtils.getViewPostKey (plugin, post));
        post.getId ();
    }

}
