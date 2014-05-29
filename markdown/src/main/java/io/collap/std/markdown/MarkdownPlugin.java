package io.collap.std.markdown;

import io.collap.resource.TemplatePlugin;
import org.parboiled.Parboiled;
import org.pegdown.LinkRenderer;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MarkdownPlugin extends TemplatePlugin {

    private final static Logger logger = Logger.getLogger (MarkdownPlugin.class.getName ());

    /**
     * Note: The Parser is not thread safe!
     * TODO: Pool multiple Parsers and Serializers based on the expected demand.
     */
    private Parser parser;
    private ToHtmlSerializer serializer;

    @Override
    public void initialize () {
        super.initialize ();
    }

    @Override
    public void destroy () {

    }

    public synchronized String convertMarkdownToHTML (String markdown) {
        /* Initialize the parser only when other plugins had the chance to add tags. */
        if (parser == null) {
            initializePegdown ();
        }

        long time = System.nanoTime ();
        RootNode rootNode = parser.parse (prepareSource (markdown));
        String html = serializer.toHtml (rootNode);
        logger.info ("Markdown generation took " + (System.nanoTime () - time) + "ns.");
        return html;
    }

    private void initializePegdown () {
        PegDownPlugins.Builder builder = PegDownPlugins.builder ();
        builder.withPlugin (TagParser.class);
        PegDownPlugins parserPlugins = builder.build ();
        parser = Parboiled.createParser (
            Parser.class,
            0,
            PegDownProcessor.DEFAULT_MAX_PARSING_TIME,
            Parser.DefaultParseRunnerProvider,
            parserPlugins
        );
        List<ToHtmlSerializerPlugin> serializerPlugins = new ArrayList<> ();
        serializerPlugins.add (new TagSerializer (this));
        serializer = new ToHtmlSerializer (new LinkRenderer (), serializerPlugins);
    }

    /**
     * Note: Taken from org.pegdown.PegDownProcessor. The respective pegdown license applies to this method.
     *       Modified by Collap.
     *
     * Adds two trailing newlines.
     *
     * @param sourceString the markdown source to process
     * @return the processed source
     */
    public char[] prepareSource (String sourceString) {
        char[] source = sourceString.toCharArray ();
        char[] out = new char[source.length + 2];
        System.arraycopy (source, 0, out, 0, source.length);
        out[source.length] = '\n';
        out[source.length + 1] = '\n';
        return out;
    }

}
