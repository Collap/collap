package io.collap.std.user;

import io.collap.Collap;
import io.collap.bryg.EnvironmentConfigurator;
import io.collap.bryg.EnvironmentCreator;
import io.collap.bryg.ModuleSourceLoader;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.model.GlobalVariableModel;
import io.collap.controller.Dispatcher;
import io.collap.controller.ProviderControllerFactory;
import io.collap.controller.provider.BrygProvider;
import io.collap.plugin.Module;
import io.collap.std.user.entity.User;
import io.collap.std.user.page.Login;
import io.collap.std.user.page.Profile;
import io.collap.std.user.page.Register;
import io.collap.std.user.util.Validator;
import org.hibernate.cfg.Configuration;

public class UserModule extends Module implements BrygProvider, EnvironmentConfigurator {

    public static final String VERSION = "0.1.1";
    public static final String ARTIFACT_NAME = "collap-std-user-" + VERSION;

    private Environment bryg;
    private Validator validator;
    private ProfileSectionProvider profileSections = new ProfileSectionProvider ();

    @Override
    public void initialize () {
        bryg = new EnvironmentCreator (collap, this).create ();

        validator = new Validator ();

        Dispatcher userDispatcher = new Dispatcher (collap);
        userDispatcher.registerControllerFactory ("register", new ProviderControllerFactory (Register.class, this));
        userDispatcher.registerControllerFactory ("login", new ProviderControllerFactory (Login.class, this));
        userDispatcher.registerControllerFactory ("profile", new ProviderControllerFactory (Profile.class, this, profileSections));
        collap.getRootDispatcher ().registerDispatcher ("user", userDispatcher);
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (User.class);
    }

    public Validator getValidator () {
        return validator;
    }

    public ProfileSectionProvider getProfileSections () {
        return profileSections;
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
        classResolver.getIncludedJarFiles ().add (Collap.ARTIFACT_NAME + ".jar");
        classResolver.getRootPackageFilter ().addSubpackageFilter ("io.collap.std.user.entity");
        classResolver.getRootPackageFilter ().addSubpackageFilter ("io.collap.controller.communication");
    }

    @Override
    public void configureGlobalVariableModel (GlobalVariableModel globalVariableModel) {

    }

}
