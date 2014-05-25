package io.collap.std.post.category;

import io.collap.controller.TemplateController;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.resource.TemplatePlugin;

import java.io.IOException;

public class EditCategory extends TemplateController {

    public EditCategory (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    public void execute (String remainingPath, Request request, Response response) throws IOException {
        response.getWriter ().write ("Hi!");
    }

}
