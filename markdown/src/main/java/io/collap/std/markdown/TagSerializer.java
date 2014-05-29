package io.collap.std.markdown;

import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TagSerializer implements ToHtmlSerializerPlugin {

    private MarkdownPlugin plugin;

    public TagSerializer (MarkdownPlugin plugin) {
        this.plugin = plugin;
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
                    Map<String, Object> model = new HashMap<> ();
                    model.put ("attributes", node.getAttributes ());
                    printer.print (plugin.renderTemplate ("wowhead/ItemLink", model));
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
    }

}
