package io.collap.std.post.cache;

import io.collap.cache.BasicInvalidator;
import io.collap.plugin.Module;
import io.collap.std.user.entity.User;
import net.sf.ehcache.Cache;

import java.util.Set;

/**
 * Invalidates all posts written by the user, when the user name has been changed.
 */
public class AuthorInvalidator extends BasicInvalidator<User> {

    public AuthorInvalidator (Module module) {
        super (module);
    }

    @Override
    public void invalidate (User user, Set<String> changedProperties) {
        Cache fragmentCache = module.getCollap ().getFragmentCache ();

        // TODO: Check if user _name_ has been changed!

        // TODO: Search for all posts where the user is the author.

        // SELECT id FROM posts WHERE author.id = user.id

        // fragmentCache.remove (KeyUtils.viewPost (id));
    }

}
