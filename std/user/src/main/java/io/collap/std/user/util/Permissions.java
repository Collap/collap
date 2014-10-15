package io.collap.std.user.util;

import io.collap.controller.communication.Request;

public class Permissions {

    public static boolean isUserLoggedIn (Request request) {
        // TODO: Transform this permission check to an annotation, if possible (See collap-core TODO).
        return request.getSessionAttribute ("user") != null;
    }

}
