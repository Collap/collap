package io.collap.resource;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import io.collap.StandardDirectories;
import io.collap.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * The template loader points to the 'res/template' directory of a plugin.
 */
public abstract class TemplatePlugin extends Plugin {

    private static final Map<String, Object> EMPTY_MODEL = new HashMap<> ();

    protected JadeConfiguration jadeConfiguration;

    @Override
    public void initialize () {
        jadeConfiguration = new JadeConfiguration ();
        jadeConfiguration.setCaching (true);
        jadeConfiguration.setPrettyPrint (true);
        String path = new File (StandardDirectories.resourceCache, name + "/template").getAbsolutePath ();
        FileTemplateLoader loader = new FileTemplateLoader (FileUtils.appendDirectorySeparator (path), "UTF-8");
        jadeConfiguration.setTemplateLoader (loader);
    }

    public String renderTemplate (String name) throws IOException {
        return renderTemplate (name, EMPTY_MODEL);
    }

    public String renderTemplate (String name, Map<String, Object> model) throws IOException {
        JadeTemplate template = jadeConfiguration.getTemplate (name);
        return jadeConfiguration.renderTemplate (template, model);
    }

    public void renderAndWriteTemplate (String name, Writer writer) throws IOException {
        renderAndWriteTemplate (name, EMPTY_MODEL, writer);
    }

    public void renderAndWriteTemplate (String name, Map<String, Object> model, Writer writer) throws IOException {
        JadeTemplate template = jadeConfiguration.getTemplate (name);
        jadeConfiguration.renderTemplate (template, model, writer);
    }

    public JadeConfiguration getJadeConfiguration () {
        return jadeConfiguration;
    }

}
