package io.collap.controller;

import io.collap.resource.Plugin;
import io.collap.template.TemplateRenderer;

import java.util.ArrayList;
import java.util.List;

public class SectionControllerFactory extends TemplateControllerFactory {

    protected List<ControllerFactory> sectionFactories = new ArrayList<> ();

    public SectionControllerFactory (Class<? extends Controller> controllerClass, Plugin plugin, TemplateRenderer renderer) {
        super (controllerClass, plugin, renderer);
    }

    public void addSectionFactory (ControllerFactory sectionFactory) {
        sectionFactories.add (sectionFactory);
    }

    @Override
    protected void configureController (Controller controller) {
        super.configureController (controller);

        List<Controller> sections = new ArrayList<> (sectionFactories.size ());
        for (ControllerFactory sectionFactory : sectionFactories) {
            sections.add (sectionFactory.createController ());
        }

        ((SectionController) controller).setSections (sections);
    }

}
