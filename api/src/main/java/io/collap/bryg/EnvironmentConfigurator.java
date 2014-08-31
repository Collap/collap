package io.collap.bryg;

import io.collap.bryg.compiler.Configuration;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.model.GlobalVariableModel;

public interface EnvironmentConfigurator {

    public SourceLoader getSourceLoader ();

    public void configureConfiguration (Configuration configuration);
    public void configureClassResolver (ClassResolver resolver);
    public void configureGlobalVariableModel (GlobalVariableModel globalVariableModel);

}
