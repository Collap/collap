package io.collap.std.main_wrapper;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.Model;
import io.collap.controller.Wrapper;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;
import io.collap.controller.provider.BrygProvider;

import java.io.IOException;

public class MainWrapper implements Wrapper {

    private Environment bryg;

    public MainWrapper (BrygProvider brygProvider) {
        bryg = brygProvider.getBryg ();
    }

    @Override
    public void execute (Response in, Request request, Response out) throws IOException {
        Model model = bryg.createModel ();
        model.setVariable ("headSrc", in.getHead ());
        model.setVariable ("content", in.getContent ());
        bryg.getTemplate ("main").render (out.getContentWriter (), model);
    }

}
