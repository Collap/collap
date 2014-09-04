package io.collap.std.post;

import io.collap.bryg.EnvironmentConfigurator;
import io.collap.bryg.EnvironmentCreator;
import io.collap.bryg.ModuleSourceLoader;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.model.GlobalVariableModel;
import io.collap.cache.InvalidatorManager;
import io.collap.controller.*;
import io.collap.controller.provider.BrygProvider;
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
import io.collap.std.user.ProfileSectionProvider;
import io.collap.std.user.UserModule;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class PostModule extends Module implements BrygProvider, EnvironmentConfigurator {

    private static final String VERSION = "0.1.1";
    private static final String ARTIFACT_NAME = "collap-std-post-" + VERSION;

    private Environment bryg;
    private Map<String, Type> postTypes;

    @Override
    public void initialize () {
        bryg = new EnvironmentCreator (collap, this).create ();
        postTypes = new HashMap<> ();

        /* Add standard post types! */
        PlainType plainType = new PlainType (collap, bryg);
        addPostType (plainType);

        /* post/ */
        Dispatcher postDispatcher = new Dispatcher (collap);
        ControllerFactory editPostControllerFactory = new ProviderControllerFactory (EditPost.class, this);
        postDispatcher.registerControllerFactory ("edit", editPostControllerFactory);
        postDispatcher.registerControllerFactory ("new", editPostControllerFactory);
        postDispatcher.registerControllerFactory ("view", new ProviderControllerFactory (ViewPost.class, this));
        postDispatcher.registerControllerFactory ("delete", new ProviderControllerFactory (DeletePost.class, this));
        postDispatcher.registerControllerFactory ("list", new ProviderControllerFactory (ListPosts.class, this));
        collap.getRootDispatcher ().registerDispatcher ("post", postDispatcher);

        /* category/ */
        Dispatcher categoryDispatcher = new Dispatcher (collap);
        ControllerFactory editCategoryControllerFactory = new ProviderControllerFactory (EditCategory.class, this);
        categoryDispatcher.registerControllerFactory ("edit", editCategoryControllerFactory);
        categoryDispatcher.registerControllerFactory ("new", editCategoryControllerFactory);
        categoryDispatcher.registerControllerFactory ("delete", new ProviderControllerFactory (DeleteCategory.class, this));
        collap.getRootDispatcher ().registerDispatcher ("category", categoryDispatcher);

        /* Add the posts section to the profile page! */
        UserModule userModule = (UserModule) collap.getPluginManager ().getPlugins ().get ("std-user");
        ProfileSectionProvider profileSections = userModule.getProfileSections ();
        if (profileSections != null) {
            profileSections.getSectionControllerFactories ().add (
                    new ProviderControllerFactory (PostsUserProfileSection.class, this)
            );
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

    @Override
    public Environment getBryg () {
        return bryg;
    }

    @Override
    public SourceLoader getSourceLoader () {
        return new ModuleSourceLoader (this);
    }

    @Override
    public void configureConfiguration (io.collap.bryg.compiler.Configuration configuration) {

    }

    @Override
    public void configureClassResolver (ClassResolver classResolver) {
        classResolver.getIncludedJarFiles ().add (ARTIFACT_NAME + ".jar");
        classResolver.getRootPackageFilter ().addSubpackageFilter (Category.class.getPackage ().getName ());
    }

    @Override
    public void configureGlobalVariableModel (GlobalVariableModel globalVariableModel) {

    }

}
