package io.collap.std.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="post_category_links")
public class CategoryLink {

    public Long postId;
    public Long categoryId;

    public CategoryLink () {

    }

    public Long getPostId () {
        return postId;
    }

    public void setPostId (Long postId) {
        this.postId = postId;
    }

    public Long getCategoryId () {
        return categoryId;
    }

    public void setCategoryId (Long categoryId) {
        this.categoryId = categoryId;
    }

}
