package io.collap.std.main_wrapper;

import io.collap.controller.Wrapper;
import io.collap.controller.communication.Request;
import io.collap.controller.communication.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainWrapper implements Wrapper {

    private MainWrapperPlugin plugin;

    public MainWrapper (MainWrapperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute (Response in, Request request, Response out) throws IOException {
        Map<String, Object> model = new HashMap<> ();
        model.put ("content", in.getContent ());
        model.put ("head", in.getHead ());
        plugin.renderAndWriteTemplate ("main", model, out.getContentWriter ());
    }

}
