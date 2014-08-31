package io.collap.std.main_wrapper;

import io.collap.bryg.EnvironmentConfigurator;
import io.collap.bryg.EnvironmentCreator;
import io.collap.bryg.ModuleSourceLoader;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.model.GlobalVariableModel;
import io.collap.controller.provider.BrygProvider;
import io.collap.plugin.Module;
import org.hibernate.cfg.Configuration;

public class MainWrapperModule extends Module implements BrygProvider, EnvironmentConfigurator {

    private Environment bryg;

    @Override
    public void configureHibernate (Configuration configuration) {

    }

    @Override
    public void initialize () {
        bryg = new EnvironmentCreator (collap, this).create ();
        collap.getRootDispatcher ().setWrapper (new MainWrapper (this));
    }

    @Override
    public void destroy () {

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

    }

    @Override
    public void configureGlobalVariableModel (GlobalVariableModel globalVariableModel) {

    }

}
