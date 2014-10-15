package io.collap.std.user;

import io.collap.controller.ControllerFactory;
import io.collap.controller.provider.SectionProvider;

import java.util.ArrayList;
import java.util.List;

public class ProfileSectionProvider implements SectionProvider {

    private List<ControllerFactory> factories = new ArrayList<> ();

    @Override
    public List<ControllerFactory> getSectionControllerFactories () {
        return factories;
    }

}
