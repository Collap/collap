package io.collap.template;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import io.collap.Collap;
import io.collap.StandardDirectories;
import io.collap.plugin.Module;
import io.collap.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;

/**
 * The implementation must be thread-safe!
 */
public class TemplateRenderer {

    protected JadeConfiguration jadeConfiguration;

    public TemplateRenderer (Module module) {
        initialize (module.getCollap (), module.getName ());
    }

    public TemplateRenderer (Collap collap, String cacheName) {
        initialize (collap, cacheName);
    }

    private void initialize (Collap collap, String cacheName) {
        jadeConfiguration = new JadeConfiguration ();
        jadeConfiguration.setCaching (true);
        jadeConfiguration.setPrettyPrint (true);
        String path = new File (StandardDirectories.resourceCache, cacheName + "/template").getAbsolutePath ();
        FileTemplateLoader loader = new FileTemplateLoader (FileUtils.appendDirectorySeparator (path), "UTF-8");
        jadeConfiguration.setTemplateLoader (loader);

        /* Set shared variables. */
        Map<String, Object> defaultModel = jadeConfiguration.getSharedVariables ();
        defaultModel.put ("basePath", collap.getBasePath ());
    }

    public String renderTemplate (String name) throws IOException {
        Map<String, Object> emptyModel = Collections.emptyMap ();
        return renderTemplate (name, emptyModel);
    }

    public String renderTemplate (String name, Map<String, Object> model) throws IOException {
        JadeTemplate template = jadeConfiguration.getTemplate (name);
        return jadeConfiguration.renderTemplate (template, model);
    }

    public void renderAndWriteTemplate (String name, Writer writer) throws IOException {
        Map<String, Object> emptyModel = Collections.emptyMap ();
        renderAndWriteTemplate (name, emptyModel, writer);
    }

    public void renderAndWriteTemplate (String name, Map<String, Object> model, Writer writer) throws IOException {
        JadeTemplate template = jadeConfiguration.getTemplate (name);
        jadeConfiguration.renderTemplate (template, model, writer);
    }

    public JadeConfiguration getJadeConfiguration () {
        return jadeConfiguration;
    }

}
