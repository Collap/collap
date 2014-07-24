package io.collap.controller;

import io.collap.plugin.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicControllerFactory implements ControllerFactory {

    private static final Logger logger = Logger.getLogger (BasicControllerFactory.class.getName ());

    protected Class<? extends Controller> controllerClass;
    protected Module module;

    public BasicControllerFactory (Class<? extends Controller> controllerClass, Module module) {
        this.controllerClass = controllerClass;
        this.module = module;
    }

    @Override
    public final Controller createController () {
        try {
            Controller controller = controllerClass.getConstructor ().newInstance ();
            configureController (controller);
            return controller;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.log (Level.SEVERE, "", e);
        }

        throw new RuntimeException ("Controller " + controllerClass.getName () + " could not be instantiated!");
    }

    /**
     * Override this method to configure the controller of a specific class.
     */
    protected void configureController (Controller controller) {
        ((BasicController) controller).setModule (module);
    }

}
