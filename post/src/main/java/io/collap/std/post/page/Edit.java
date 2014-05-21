package io.collap.std.post.page;

import io.collap.controller.TemplateController;
import io.collap.resource.TemplatePlugin;
import io.collap.std.entity.Post;
import io.collap.std.entity.User;
import io.collap.std.markdown.MarkdownPlugin;
import io.collap.std.post.util.PostUtil;
import io.collap.util.TransactionHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * GET:
 *      Either write a new post or edit a post that already exists.
 *      The remainingPath determines whether a new post is created: When an ID is supplied, the post with that ID is edited.
 *      Otherwise, a new post is created.
 * POST:
 *      Update or add a post.
 * A post with an ID of -1 is considered a non-existent post and signals a newly created post.
 */
public class Edit extends TemplateController {

    public Edit (TemplatePlugin plugin) {
        super (plugin);
    }

    @Override
    public void execute (Type type, String remainingPath, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession ();
        if (session == null || session.getAttribute ("user") == null) {
            response.getWriter ().write ("You need to be logged in!");
            return;
        }

        if (type == Type.get) {
            Post post = PostUtil.getPostFromDatabase (plugin.getCollap (), remainingPath, true);
            if (post != null) {
                User author = (User) session.getAttribute ("user");
                if (post.getId () == -1 || author.getId () == post.getAuthorId ()) {
                    Map<String, Object> model = new HashMap<> ();
                    model.put ("post", post);
                    plugin.renderAndWriteTemplate ("Edit", model, response.getWriter ());
                }else {
                    response.getWriter ().write ("Insufficient editing permissions!");
                }
            }else {
                // TODO: Potential source of knowledge for an outsider of which IDs are taken.
                response.getWriter ().write ("Post not found!");
            }
        }else if (type == Type.post) {
            editPost (request, response);
        }
    }

    private void editPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Possible validation.
        long id = -1;
        try {
            id = Long.parseLong (request.getParameter ("id"));
        } catch (NumberFormatException ex) {
            response.getWriter ().write ("Hidden 'id' input field supplied a wrong number!");
            return;
        }

        /* Note: It is assumed that a check whether a user is logged in already passed. */
        User author = (User) request.getSession ().getAttribute ("user");

        TransactionHelper transactionHelper = plugin.getCollap ().getTransactionHelper ();
        Date now = new Date ();
        Post post = null;
        boolean update = false;
        if (id == -1) { /* Create new post! */
            post = new Post ();
            post.setAuthorId (author.getId ());
            post.setPublishingDate (now);
        }else {
            post = transactionHelper.load (id, Post.class);
            if (post == null) {
                response.getWriter ().write ("Post to edit could not be found!");
                return;
            }

            /* Validate author. */
            if (!post.getAuthorId ().equals (author.getId ())) {
                response.getWriter ().write ("Insufficient rights to edit the post!");
                return;
            }

            update = true;
        }

        post.setTitle (request.getParameter ("title"));
        post.setContent (request.getParameter ("content"));
        post.setLastEdit (now);

        MarkdownPlugin markdownPlugin = (MarkdownPlugin) plugin.getCollap ().getPluginManager ().getPlugins ().get ("std-markdown");
        post.setCompiledContent (markdownPlugin.convertMarkdownToHTML (post.getContent ()));

        /* Update post. */
        boolean success;
        if (update) {
            success = transactionHelper.update (post);
        }else {
            success = transactionHelper.save (post);
        }

        if (success) {
            response.getWriter ().write ("Post successfully created or updated!");
        }else {
            response.getWriter ().write ("An error occurred while creating or updating the post!");
        }
    }

}
