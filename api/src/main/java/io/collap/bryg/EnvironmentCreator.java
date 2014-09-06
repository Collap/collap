package io.collap.bryg;

import io.collap.Collap;
import io.collap.StandardDirectories;
import io.collap.bryg.compiler.Configuration;
import io.collap.bryg.compiler.StandardCompiler;
import io.collap.bryg.compiler.resolver.ClassNameFinder;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.environment.StandardEnvironment;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.loader.TemplateClassLoader;
import io.collap.bryg.model.GlobalVariableModel;

/**
 * This creator automatically:
 *   - Includes the collap-api jar and the artifact jar of the configurator,
 *     if provided.
 *   - Adds the collap/module and collap/lib directories to the finder paths.
 * Additionally, the following global variables are declared:
 *   - String basePath: The base path to the root dispatcher.
 * The EnvironmentConfigurator can further configure the bryg environment.
 */
public class EnvironmentCreator {

    private Collap collap;
    private EnvironmentConfigurator environmentConfigurator;

    public EnvironmentCreator (Collap collap, EnvironmentConfigurator environmentConfigurator) {
        this.collap = collap;
        this.environmentConfigurator = environmentConfigurator;
    }

    public Environment create () {
        SourceLoader loader = environmentConfigurator.getSourceLoader ();

        Configuration configuration = new Configuration ();
        environmentConfigurator.configureConfiguration (configuration);

        ClassResolver classResolver = new ClassResolver (collap.getBrygClassResolver ());
        ClassNameFinder classNameFinder = classResolver.getFinder ();
        classNameFinder.addPath (StandardDirectories.module.getPath () + "/*");
        classNameFinder.addPath (StandardDirectories.base.getPath () + "/lib/*");

        /* Include api jar and configurator jar. */
        classResolver.includeJar (Collap.ARTIFACT_NAME + ".jar");
        String artifactName = environmentConfigurator.getArtifactName ();
        if (artifactName != null) {
            classResolver.includeJar (artifactName + ".jar");
        }

        environmentConfigurator.configureClassResolver (classResolver);
        classResolver.resolveClassNames ();

        GlobalVariableModel globalVariableModel = new GlobalVariableModel ();
        globalVariableModel.declareVariable ("basePath", String.class, collap.getBasePath ());
        environmentConfigurator.configureGlobalVariableModel (globalVariableModel);

        io.collap.bryg.compiler.Compiler compiler = new StandardCompiler (configuration, collap.getBrygLibrary (),
                classResolver, globalVariableModel);
        TemplateClassLoader classLoader = new TemplateClassLoader (compiler, loader);

        return new StandardEnvironment (classLoader, globalVariableModel);
    }

}
