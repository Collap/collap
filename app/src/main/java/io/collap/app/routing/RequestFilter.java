package io.collap.app.routing;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestFilter implements Filter {

    @Override
    public void init (FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI ().substring (req.getContextPath ().length ());

        if (path.startsWith ("/static")) { // TODO: Is also true for any /static*, e.g. /statics or /statice
            /* Send to default servlet. */
            chain.doFilter (request, response);
        }else {
            request.getRequestDispatcher ("/servlet" + path).forward (request, response);
        }
    }

    @Override
    public void destroy () {

    }

}
