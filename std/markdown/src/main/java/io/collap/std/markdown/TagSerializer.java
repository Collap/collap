package io.collap.std.markdown;

import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.BasicModel;
import io.collap.bryg.model.Model;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.io.IOException;
import java.util.logging.Logger;

public class TagSerializer implements ToHtmlSerializerPlugin {

    private static final Logger logger = Logger.getLogger (TagSerializer.class.getName ());

    private Environment bryg;

    public TagSerializer (Environment bryg) {
        this.bryg = bryg;
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
                long time = System.nanoTime ();
                Model model = new BasicModel ();
                model.setVariable ("attributes", node.getAttributes ());
                try {
                    bryg.getTemplate ("wowhead.ItemLink").render (new PrinterWriter (printer), model);
                }catch (IOException e) {
                    e.printStackTrace (); // TODO: Handle
                }
                logger.info ("Template execution took " + (System.nanoTime () - time) + "ns.");
            }
        }
    }

}
