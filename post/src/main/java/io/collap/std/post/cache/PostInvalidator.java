package io.collap.std.post.cache;

import io.collap.cache.BasicInvalidator;
import io.collap.plugin.Module;
import io.collap.std.post.entity.Post;
import net.sf.ehcache.Cache;

public class PostInvalidator extends BasicInvalidator {

    public PostInvalidator (Module module) {
        super (module);
    }

    @Override
    public void invalidate (Object entity) {
        Post post = (Post) entity;

        /* Invalidate ViewPost pages. */
        Cache fragmentCache = module.getCollap ().getFragmentCache ();
        fragmentCache.remove (KeyUtils.getViewPostKey (module, post));
    }

}
