package io.collap.std.markdown;

import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.StringVar;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;
import org.pegdown.plugins.InlinePluginParser;
import org.pegdown.plugins.PegDownPlugins;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@BuildParseTree
public class TagParser extends Parser implements InlinePluginParser {

    private static final String[] tagPrefixes = {
        "wow"
    };

    private static HashMap<String, String[]> tagNames = new HashMap<> ();

    static {
        tagNames.put ("wow", new String[] {
            "item"
        });
    }

    public TagParser () {
        super (0, PegDownProcessor.DEFAULT_MAX_PARSING_TIME, Parser.DefaultParseRunnerProvider, PegDownPlugins.NONE);
    }

    public Rule Tag () {
        StringVar tagPrefix = new StringVar ();
        StringVar tagName = new StringVar ();
        Map<String, String> attributes = new HashMap<> ();
        return Sequence (
            '<',
            Spn1 (),
            TagPrefix (tagPrefix),
            Spn1 (),
            ':',
            Spn1 (),
            TagName (tagPrefix, tagName),
            Spn1 (),
            TagAttributes (attributes),
            Optional ('/'),
            Spn1 (),
            '>',
            push (new TagNode (tagPrefix.get (), tagName.get (), attributes))
        );
    }

    public Rule TagAttributes (Map<String, String> attributes) {
        return ZeroOrMore (TagAttribute (attributes));
    }

    public Rule TagAttribute (final Map<String, String> attributes) {
        final StringVar name = new StringVar ();
        final StringVar value = new StringVar ();

        return Sequence (
            OneOrMore (FirstOf (Alphanumeric (), '-', '_')),
            name.set (match ()),
            Spn1 (),
            Optional (
                '=',
                Spn1 (),
                FirstOf (QuotedValue (value), OneOrMore (TestNot ('>'), Nonspacechar ()))
            ),
            new Action () {
                @Override
                public boolean run (Context context) {
                    attributes.put (name.get (), value.get ());
                    return true;
                }
            },
            Spn1()
        );
    }

    public Rule TagPrefix (StringVar prefix) {
        return Sequence (
            OneOrMore (Alphanumeric ()),
            prefix.set (match ()) && isTagPrefix (prefix.get ())
        );
    }

    public boolean isTagPrefix (String prefix) {
        return Arrays.binarySearch (tagPrefixes, prefix) >= 0;
    }

    public Rule TagName (StringVar prefix, StringVar name) {
        return Sequence (
            OneOrMore (Alphanumeric ()),
            name.set (match ()) && isTagName (prefix.get (), name.get ())
        );
    }

    public boolean isTagName (String prefix, String name) {
        String[] names = tagNames.get (prefix);
        if (names == null) return false;
        return Arrays.binarySearch (names, name) >= 0;
    }




    /********** Utility **********/

    /**
     * Sets value to the string inside the quotes, excluding the quotes themselves.
     */
    public Rule QuotedValue (StringVar value) {
        return FirstOf(
            Sequence (
                '"',
                ZeroOrMore ( TestNot('"'), ANY),
                value.set (match ()),
                '"'
            ),
            Sequence (
                '\'',
                ZeroOrMore (TestNot ('\''), ANY),
                value.set (match ()),
                '\''
            )
        );
    }


    @Override
    public Rule[] inlinePluginRules () {
        return new Rule[] {
            Tag ()
        };
    }

}
