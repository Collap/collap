package io.collap.std.post;

import io.collap.cache.InvalidatorManager;
import io.collap.controller.Dispatcher;
import io.collap.resource.TemplatePlugin;
import io.collap.std.post.cache.PostInvalidator;
import io.collap.std.post.category.DeleteCategory;
import io.collap.std.post.category.EditCategory;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import io.collap.std.post.post.*;
import io.collap.std.user.UserPlugin;
import io.collap.std.user.page.Profile;
import org.hibernate.cfg.Configuration;

public class PostPlugin extends TemplatePlugin {

    @Override
    public void initialize () {
        super.initialize ();

        /* post/ */
        Dispatcher postDispatcher = new Dispatcher (collap);
        EditPost editPostController = new EditPost (this);
        postDispatcher.registerController ("edit", editPostController);
        postDispatcher.registerController ("new", editPostController);
        postDispatcher.registerController ("view", new ViewPost (this));
        postDispatcher.registerController ("delete", new DeletePost (this));
        postDispatcher.registerController ("list", new ListPosts (this));
        collap.getRootDispatcher ().registerController ("post", postDispatcher);

        /* category/ */
        Dispatcher categoryDispatcher = new Dispatcher (collap);
        EditCategory editCategoryController = new EditCategory (this);
        categoryDispatcher.registerController ("edit", editCategoryController);
        categoryDispatcher.registerController ("new", editCategoryController);
        categoryDispatcher.registerController ("delete", new DeleteCategory (this));
        collap.getRootDispatcher ().registerController ("category", categoryDispatcher);

        /* Add the posts section to the profile page! */
        UserPlugin userPlugin = (UserPlugin) collap.getPluginManager ().getPlugins ().get ("std-user");
        Profile profilePage = userPlugin.getProfilePage ();
        if (profilePage != null) {
            profilePage.addSection (new PostsUserProfileSection (this));
        }

        /* Register invalidators! */
        InvalidatorManager invalidatorManager = collap.getInvalidatorManager ();
        invalidatorManager.addInvalidator (Post.class, new PostInvalidator (this));
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (Post.class);
        cfg.addAnnotatedClass (Category.class);
    }

}
