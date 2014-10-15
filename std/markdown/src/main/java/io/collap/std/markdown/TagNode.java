package io.collap.std.markdown;

import org.parboiled.common.ImmutableList;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagNode extends AbstractNode {

    private String prefix;
    private String name;
    private Map<String, String> attributes = new HashMap<> (); // name -> value

    public TagNode (String prefix, String name, Map<String, String> attributes) {
        this.prefix = prefix;
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public void accept (Visitor visitor) {
        visitor.visit (this);
    }

    @Override
    public List<Node> getChildren () {
        return ImmutableList.of ();
    }

    public String getPrefix () {
        return prefix;
    }

    public String getName () {
        return name;
    }

    public Map<String, String> getAttributes () {
        return attributes;
    }

}
