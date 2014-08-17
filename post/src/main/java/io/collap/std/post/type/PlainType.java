package io.collap.std.post.type;

import io.collap.Collap;
import io.collap.controller.communication.Request;
import io.collap.entity.Entity;
import io.collap.std.markdown.MarkdownModule;
import io.collap.std.post.entity.PlainData;
import io.collap.std.post.entity.Post;
import io.collap.template.TemplateRenderer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlainType extends BasicType {

    private TemplateRenderer renderer;

    public PlainType (Collap collap, TemplateRenderer renderer) {
        super (collap);
        this.renderer = renderer;
    }

    @Override
    protected void update (Entity dataEntity, Request request) {
        PlainData data = (PlainData) dataEntity;
        data.setTitle (request.getStringParameter ("title"));
        data.setSource (request.getStringParameter ("source"));
    }

    @Override
    protected String getEditor (@Nullable Entity dataEntity) {
        PlainData data = (PlainData) dataEntity;
        if (data == null) {
            data = PlainData.createTransientPlainData ();
        }

        Map<String, Object> model = new HashMap<> ();
        model.put ("data", data);
        try {
            return renderer.renderTemplate ("type/plain/Editor", model);
        } catch (IOException e) {
            throw new RuntimeException ("Plain/Editor template not found!", e);
        }
    }

    @Override
    public void compile (Entity entity, Post post) {
        PlainData data = (PlainData) entity;
        MarkdownModule markdownModule = (MarkdownModule) collap.getPluginManager ().getPlugins ().get ("std-markdown");
        post.setContent (markdownModule.convertMarkdownToHTML (data.getSource ()));
        post.setTitle (data.getTitle ());
    }

    @Override
    public String getName () {
        return "plain";
    }

    @Override
    protected Class<? extends Entity> getDataClass () {
        return PlainData.class;
    }

}
