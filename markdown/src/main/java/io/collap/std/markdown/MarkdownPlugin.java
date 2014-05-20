package io.collap.std.markdown;

import io.collap.resource.Plugin;
import org.pegdown.PegDownProcessor;

public class MarkdownPlugin extends Plugin {

    private PegDownProcessor pegDownProcessor;

    @Override
    public void initialize () {
        pegDownProcessor = new PegDownProcessor ();
    }

    @Override
    public void destroy () {

    }

    public String convertMarkdownToHTML (String markdown) {
        return pegDownProcessor.markdownToHtml (markdown);
    }

}
