package io.collap.controller.section;

import io.collap.controller.Controller;
import io.collap.controller.communication.InternalRequest;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.SectionDependant;

import java.io.IOException;

public class Section {

    private SectionDependant dependant;
    private Controller controller;

    public Section (SectionDependant dependant, Controller controller) {
        this.dependant = dependant;
        this.controller = controller;
    }

    public Response execute (Request parentRequest) throws IOException {
        /* Pass session if possible. */
        InternalRequest sectionRequest;
        if (parentRequest.getHttpSession () != null) {
            sectionRequest =  new InternalRequest (Request.Method.get, parentRequest.getHttpSession ());
        }else {
            sectionRequest = new InternalRequest (Request.Method.get);
        }

        Response sectionResponse = new Response ();
        dependant.configureSectionRequest (sectionRequest);

        controller.initialize (sectionRequest, "");
        controller.doGet (sectionResponse);

        // TODO: Use handleError function of the section correctly!

        return sectionResponse;
    }

}
