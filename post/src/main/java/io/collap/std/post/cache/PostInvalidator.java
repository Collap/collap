package io.collap.std.post.cache;

import io.collap.cache.BasicInvalidator;
import io.collap.plugin.Module;
import io.collap.std.post.entity.Post;
import net.sf.ehcache.Cache;

import java.util.Set;

public class PostInvalidator extends BasicInvalidator<Post> {

    public PostInvalidator (Module module) {
        super (module);
    }

    @Override
    public void invalidate (Post post, Set<String> changedProperties) {
        /* Invalidate ViewPost pages. */
        Cache fragmentCache = module.getCollap ().getFragmentCache ();
        fragmentCache.remove (KeyUtils.viewPost (post));
    }

}
