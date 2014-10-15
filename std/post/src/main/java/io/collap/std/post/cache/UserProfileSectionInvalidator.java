package io.collap.std.post.cache;

import io.collap.cache.BasicInvalidator;
import io.collap.plugin.Module;
import io.collap.std.post.entity.Post;
import net.sf.ehcache.Cache;

import java.util.Set;
import java.util.logging.Logger;

public class UserProfileSectionInvalidator extends BasicInvalidator<Post> {

    public UserProfileSectionInvalidator (Module module) {
        super (module);
    }

    @Override
    public void invalidate (Post post, Set<String> changedProperties) {
        for (String propName : changedProperties) {
            Logger.getLogger (this.getClass ().getName ()).info ("Changed property: " + propName);
        }

        /* The user profile section only shows the title. */
        if (!changedProperties.contains ("title")) {
            Logger.getLogger (getClass ().getName ()).info ("The title of the post has not been changed, so " +
                    "the post profile section does not need to be rendered again.");
            return;
        }

        Cache fragmentCache = module.getCollap ().getFragmentCache ();
        Logger.getLogger (this.getClass ().getName ()).info ("Invalidate " + post.getId ());
        fragmentCache.remove (io.collap.std.user.cache.KeyUtil.userProfile (post.getAuthor ()));
    }

}
