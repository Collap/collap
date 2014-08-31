package io.collap.controller;

import io.collap.controller.provider.*;
import io.collap.controller.section.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProviderControllerFactory implements ControllerFactory {

    private static final Logger logger = Logger.getLogger (ProviderControllerFactory.class.getName ());

    public interface DependencySetter<P extends Provider, D extends Dependant> {

        public void setDependency (P provider, D dependant);

        public Class<P> getProviderType ();
        public Class<D> getDependantType ();

    }

    private static List<DependencySetter> dependencySetters = new ArrayList<DependencySetter> () {{
        add (new DependencySetter<ModuleProvider, ModuleDependant> () {
            @Override
            public void setDependency (ModuleProvider provider, ModuleDependant dependant) {
                dependant.setModule (provider.getModule ());
            }

            @Override
            public Class<ModuleProvider> getProviderType () {
                return ModuleProvider.class;
            }

            @Override
            public Class<ModuleDependant> getDependantType () {
                return ModuleDependant.class;
            }
        });

        add (new DependencySetter<BrygProvider, BrygDependant> () {
            @Override
            public void setDependency (BrygProvider provider, BrygDependant dependant) {
                dependant.setBryg (provider.getBryg ());
            }

            @Override
            public Class<BrygProvider> getProviderType () {
                return BrygProvider.class;
            }

            @Override
            public Class<BrygDependant> getDependantType () {
                return BrygDependant.class;
            }
        });

        add (new DependencySetter<SectionProvider, SectionDependant> () {
            @Override
            public void setDependency (SectionProvider provider, SectionDependant dependant) {
                List<ControllerFactory> factories = provider.getSectionControllerFactories ();
                List<Section> sections = new ArrayList<> (factories.size ());
                for (ControllerFactory factory : factories) {
                    Section section = new Section (dependant, factory.createController ());
                    sections.add (section);
                }
                dependant.setSections (sections);
            }

            @Override
            public Class<SectionProvider> getProviderType () {
                return SectionProvider.class;
            }

            @Override
            public Class<SectionDependant> getDependantType () {
                return SectionDependant.class;
            }
        });

        add (new DependencySetter<JadeProvider, JadeDependant> () {
            @Override
            public void setDependency (JadeProvider provider, JadeDependant dependant) {
                dependant.setRenderer (provider.getRenderer ());
            }

            @Override
            public Class<JadeProvider> getProviderType () {
                return JadeProvider.class;
            }

            @Override
            public Class<JadeDependant> getDependantType () {
                return JadeDependant.class;
            }
        });
    }};

    private Class<? extends Controller> controllerClass;
    private Provider[] providers;

    public ProviderControllerFactory (Class<? extends Controller> controllerClass, Provider... providers) {
        this.controllerClass = controllerClass;
        this.providers = providers;
    }

    @Override
    public Controller createController () {
        Controller controller;
        try {
            controller = controllerClass.newInstance ();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.log (Level.SEVERE, "Could not load controller by controller class " + controllerClass.getName (), e);
            return null;
        }

        /* Check all possible providers/dependant interfaces. */
        for (DependencySetter setter : dependencySetters) {
            if (setter.getDependantType ().isInstance (controller)) {
                boolean success = invokeSetter ((Dependant) controller, setter);
                if (!success) {
                    logger.severe ("The controller " + controllerClass.getName () +
                            ", which implements the " + setter.getDependantType ().getSimpleName () + " interface, " +
                            "must have one provider that implements the "
                            + setter.getProviderType ().getSimpleName () + " interface!");
                    return null;
                }
            }
        }

        return controller;
    }

    /**
     * @return Whether the dependency was set successfully.
     */
    private boolean invokeSetter (Dependant dependant, DependencySetter setter) {
        /* Check all providers and choose the first one with the right type. */
        for (Provider provider : providers) {
            if (setter.getProviderType ().isInstance (provider)) {
                setter.setDependency (provider, dependant);
                return true;
            }
        }

        return false;
    }

}
