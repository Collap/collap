package io.collap.std.post.type;

import io.collap.Collap;
import io.collap.bryg.environment.Environment;
import io.collap.bryg.model.BasicModel;
import io.collap.bryg.model.Model;
import io.collap.controller.communication.Request;
import io.collap.entity.Entity;
import io.collap.std.markdown.MarkdownModule;
import io.collap.std.post.entity.PlainData;
import io.collap.std.post.entity.Post;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringWriter;

public class PlainType extends BasicType {

    private Environment bryg;

    public PlainType (Collap collap, Environment bryg) {
        super (collap);
        this.bryg = bryg;
    }

    @Override
    protected void update (Entity dataEntity, Request request) {
        PlainData data = (PlainData) dataEntity;
        data.setTitle (request.getStringParameter ("title"));
        data.setSource (request.getStringParameter ("source"));
    }

    @Override
    protected String getEditor (@Nullable Entity dataEntity) throws IOException {
        PlainData data = (PlainData) dataEntity;
        if (data == null) {
            data = PlainData.createTransientPlainData ();
        }

        Model model = new BasicModel ();
        model.setVariable ("data", data);

        StringWriter writer = new StringWriter ();
        bryg.getTemplate ("type.plain.Editor").render (writer, model);
        return writer.toString ();
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
