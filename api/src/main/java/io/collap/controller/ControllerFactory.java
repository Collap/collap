package io.collap.controller;

/**
 * The implementation must be thread-safe!
 */
public interface ControllerFactory {

    public Controller createController ();

}
