package io.collap.std.post;

import io.collap.cache.InvalidatorManager;
import io.collap.controller.*;
import io.collap.plugin.Module;
import io.collap.std.post.cache.PostInvalidator;
import io.collap.std.post.category.DeleteCategory;
import io.collap.std.post.category.EditCategory;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.Post;
import io.collap.std.post.post.*;
import io.collap.std.user.UserModule;
import io.collap.template.TemplateRenderer;
import org.hibernate.cfg.Configuration;

public class PostModule extends Module {

    private TemplateRenderer renderer;

    @Override
    public void initialize () {
        renderer = new TemplateRenderer (this);

        /* post/ */
        Dispatcher postDispatcher = new Dispatcher (collap);
        ControllerFactory editPostControllerFactory = new TemplateControllerFactory (EditPost.class, this, renderer);
        postDispatcher.registerControllerFactory ("edit", editPostControllerFactory);
        postDispatcher.registerControllerFactory ("new", editPostControllerFactory);
        postDispatcher.registerControllerFactory ("view", new TemplateControllerFactory (ViewPost.class, this, renderer));
        postDispatcher.registerControllerFactory ("delete", new TemplateControllerFactory (DeletePost.class, this, renderer));
        postDispatcher.registerControllerFactory ("list", new TemplateControllerFactory (ListPosts.class, this, renderer));
        collap.getRootDispatcher ().registerDispatcher ("post", postDispatcher);

        /* category/ */
        Dispatcher categoryDispatcher = new Dispatcher (collap);
        ControllerFactory editCategoryControllerFactory = new TemplateControllerFactory (EditCategory.class, this, renderer);
        categoryDispatcher.registerControllerFactory ("edit", editCategoryControllerFactory);
        categoryDispatcher.registerControllerFactory ("new", editCategoryControllerFactory);
        categoryDispatcher.registerControllerFactory ("delete", new TemplateControllerFactory (DeleteCategory.class, this, renderer));
        collap.getRootDispatcher ().registerDispatcher ("category", categoryDispatcher);

        /* Add the posts section to the profile page! */
        UserModule userModule = (UserModule) collap.getPluginManager ().getPlugins ().get ("std-user");
        SectionControllerFactory profileSectionControllerFactory = userModule.getProfileSectionControllerFactory ();
        if (profileSectionControllerFactory != null) {
            profileSectionControllerFactory.addSectionFactory (new TemplateControllerFactory (PostsUserProfileSection.class,
                    this, renderer));
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
