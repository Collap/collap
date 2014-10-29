package io.collap.std.post.type;

import javax.annotation.Nullable;
import io.collap.controller.communication.Request;
import io.collap.std.post.entity.Post;

import java.io.IOException;

public interface Type {

    /**
     * Creates or updates the custom post data.
     * @param dataId May be null, in which case new post data is created.
     * @return The ID of the custom post data.
     */
    public Long update (@Nullable Long dataId, Request request);

    /**
     * @param dataId This parameter is nullable because an editor should exist for non-existent post data.
     */
    public String getEditor (@Nullable Long dataId) throws IOException;

    /**
     * This method should set post.content and post.title!
     */
    public void compile (Post post) throws IOException;

    /**
     * Deletes the data associated to the post.
     */
    public void deleteData (Post post);

    public String getName ();

}
