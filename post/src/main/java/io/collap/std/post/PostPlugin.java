package io.collap.std.post;

import io.collap.controller.Dispatcher;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.Post;
import io.collap.std.post.page.Edit;
import org.hibernate.cfg.Configuration;

public class PostPlugin extends TemplatePlugin {

    @Override
    public void initialize () {
        super.initialize ();

        Dispatcher postDispatcher = new Dispatcher ();
        Edit editController = new Edit (this);
        postDispatcher.registerController ("edit", editController);
        postDispatcher.registerController ("new", editController);
        collap.getRootDispatcher ().registerController ("post", postDispatcher);
    }

    @Override
    public void destroy () {

    }

    @Override
    public void configureHibernate (Configuration cfg) {
        cfg.addAnnotatedClass (Post.class);
    }

}
