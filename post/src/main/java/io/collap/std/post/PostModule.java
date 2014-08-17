package io.collap.std.post;

import io.collap.cache.InvalidatorManager;
import io.collap.controller.*;
import io.collap.plugin.Module;
import io.collap.std.post.cache.PostInvalidator;
import io.collap.std.post.category.DeleteCategory;
import io.collap.std.post.category.EditCategory;
import io.collap.std.post.entity.Category;
import io.collap.std.post.entity.PlainData;
import io.collap.std.post.entity.Post;
import io.collap.std.post.post.*;
import io.collap.std.post.type.PlainType;
import io.collap.std.post.type.Type;
import io.collap.std.user.UserModule;
import io.collap.template.TemplateRenderer;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class PostModule extends Module {

    private TemplateRenderer renderer;
    private Map<String, Type> postTypes;

    @Override
    public void initialize () {
        renderer = new TemplateRenderer (this);
        postTypes = new HashMap<> ();

        /* Add standard post types! */
        PlainType plainType = new PlainType (collap, renderer);
        addPostType (plainType);

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
        cfg.addAnnotatedClass (PlainData.class);
    }

    /**
     * This method is intended for read use <b>only</b>!
     */
    public Map<String, Type> getPostTypes () {
        return postTypes;
    }

    public void addPostType (Type type) {
        postTypes.put (type.getName (), type);
    }

}
