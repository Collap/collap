package io.collap.controller.communication;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse extends Response {

    private static Logger logger = Logger.getLogger (HttpResponse.class.getName ());

    private Writer writer;

    public HttpResponse (HttpServletResponse httpServletResponse) {
        try {
            writer = httpServletResponse.getWriter ();
        } catch (IOException e) {
            logger.log (Level.SEVERE, "An exception occurred while getting the HttpServletResponse Writer!", e);
        }
    }

    @Override
    public Writer getWriter () {
        return writer;
    }

}
