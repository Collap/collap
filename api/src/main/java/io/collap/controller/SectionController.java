package io.collap.controller;

import io.collap.controller.communication.InternalRequest;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;
import java.util.List;

public abstract class SectionController extends TemplateController {

    protected List<Controller> sections;

    protected abstract void configureSectionRequest (InternalRequest sectionRequest);

    protected Response executeSection (Controller section) throws IOException {
        InternalRequest sectionRequest = new InternalRequest (Request.Method.get);
        Response sectionResponse = new Response ();
        configureSectionRequest (sectionRequest);

        section.initialize ("", sectionRequest);
        section.doGet (sectionResponse);

        // TODO: Use handleError function of the section correctly!

        return sectionResponse;
    }

    public void setSections (List<Controller> sections) {
        this.sections = sections;
    }

}
