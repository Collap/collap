package io.collap.std.markdown;

import io.collap.template.TemplateRenderer;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TagSerializer implements ToHtmlSerializerPlugin {

    private static final Logger logger = Logger.getLogger (TagSerializer.class.getName ());

    private TemplateRenderer renderer;

    public TagSerializer (TemplateRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public boolean visit (Node node, Visitor visitor, Printer printer) {
        if (node instanceof TagNode) {
            visitTagNode ((TagNode) node, printer);
            return true;
        }
        return false;
    }

    private void visitTagNode (TagNode node, Printer printer) {
        // TODO: NOTE: This is only a temporary test.
        if (node.getPrefix ().equals ("wow")) {
            if (node.getName ().equals ("item")) {
                try {
                    long time = System.nanoTime ();
                    Map<String, Object> model = new HashMap<> ();
                    model.put ("attributes", node.getAttributes ());
                    printer.print (renderer.renderTemplate ("wowhead/ItemLink", model));
                    logger.info ("Template execution took " + (System.nanoTime () - time) + "ns.");
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }

}
