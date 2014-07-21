package io.collap.controller;

import io.collap.resource.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class requires the controller class to implement Controller (Plugin) as a constructor.
 */
public class BasicControllerFactory implements ControllerFactory {

    private static final Logger logger = Logger.getLogger (BasicControllerFactory.class.getName ());

    protected Class<? extends Controller> controllerClass;
    protected Plugin plugin;

    public BasicControllerFactory (Class<? extends Controller> controllerClass, Plugin plugin) {
        this.controllerClass = controllerClass;
        this.plugin = plugin;
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
        ((BasicController) controller).setPlugin (plugin);
    }

}
