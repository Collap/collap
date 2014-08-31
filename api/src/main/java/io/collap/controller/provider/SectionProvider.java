package io.collap.controller.provider;

import io.collap.controller.ControllerFactory;

import java.util.List;

public interface SectionProvider extends Provider {

    public List<ControllerFactory> getSectionControllerFactories ();

}
