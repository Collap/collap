package io.collap.controller.provider;

import io.collap.controller.communication.InternalRequest;
import io.collap.controller.section.Section;

import java.util.List;

public interface SectionDependant extends Dependant {

    public void setSections (List<Section> sections);

    public void configureSectionRequest (InternalRequest sectionRequest);

}
