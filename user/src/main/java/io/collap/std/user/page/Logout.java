package io.collap.std.user.page;

import io.collap.controller.ModuleController;
import io.collap.controller.communication.Response;
import io.collap.std.user.util.Permissions;

import java.io.IOException;

public class Logout extends ModuleController {

    @Override
    public void doGet (Response response) throws IOException {
        response.getHeadWriter ().write ("<title>Logout</title>");
        if (!Permissions.isUserLoggedIn (request)) {
            response.getContentWriter ().write ("You are not logged in.");
        }else {
            request.setSessionAttribute ("user", null);
            response.getContentWriter ().write ("Logged out successfully.");
        }
    }

}
