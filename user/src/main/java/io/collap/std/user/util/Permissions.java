package io.collap.std.user.util;

import io.collap.controller.communication.Request;

import javax.servlet.http.HttpSession;

public class Permissions {

    public static boolean isUserLoggedIn (Request request) {
        // TODO: Transform this permission check to an annotation, if possible (See collap-core TODO).
        HttpSession httpSession = request.getHttpRequest ().getSession ();
        return httpSession != null && httpSession.getAttribute ("user") != null;
    }

}
