package io.collap.std.markdown;

import io.collap.bryg.EnvironmentConfigurator;
import io.collap.bryg.EnvironmentCreator;
import io.collap.bryg.ModuleSourceLoader;
import io.collap.bryg.compiler.resolver.ClassResolver;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.loader.SourceLoader;
import io.collap.bryg.model.GlobalVariableModel;
import io.collap.plugin.Module;
import org.hibernate.cfg.Configuration;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ProfilingParseRunner;
import org.pegdown.LinkRenderer;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MarkdownModule extends Module implements EnvironmentConfigurator {

    private final static Logger logger = Logger.getLogger (MarkdownModule.class.getName ());

    /**
     * Note: The Parser is not thread safe!
     * TODO: Pool multiple Parsers and Serializers based on the expected demand.
     */
    private Environment bryg;
    private Parser parser;
    private List<ToHtmlSerializerPlugin> serializerPlugins;
    private ProfilingParseRunner<Node> parseRunner;

    @Override
    public void configureHibernate (Configuration configuration) {

    }

    @Override
    public void initialize () {
        bryg = new EnvironmentCreator (collap, this).create ();
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
        logger.info ("Parsing took " + (System.nanoTime () - time) + "ns.");
        time = System.nanoTime ();
        // TODO: The serializer needs to be recreated every time it is used to reset its state. This can possibly be optimized.
        String html = new ToHtmlSerializer (new LinkRenderer (), serializerPlugins).toHtml (rootNode);
        logger.info ("Printing took " + (System.nanoTime () - time) + "ns.");
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
        serializerPlugins = new ArrayList<> ();
        serializerPlugins.add (new TagSerializer (bryg));
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
    private char[] prepareSource (String sourceString) {
        char[] source = sourceString.toCharArray ();
        char[] out = new char[source.length + 2];
        System.arraycopy (source, 0, out, 0, source.length);
        out[source.length] = '\n';
        out[source.length + 1] = '\n';
        return out;
    }

    @Override
    public SourceLoader getSourceLoader () {
        return new ModuleSourceLoader (this);
    }

    @Override
    public void configureConfiguration (io.collap.bryg.compiler.Configuration configuration) {

    }

    @Override
    public void configureClassResolver (ClassResolver classResolver) {

    }

    @Override
    public void configureGlobalVariableModel (GlobalVariableModel globalVariableModel) {

    }

}
