package io.collap.std.post.entity;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

/**
 * An ID of -1 indicates a post that has not been added to the database yet.
 */
@Entity
@Table(name = "post_categories")
public class Category extends io.collap.entity.Entity {

    private String name;

    /**
     * The inverse to the categories attribute in Post.
     */
    private Set<Post> posts;

    public Category () {

    }

    /**
     * Creates an empty category that has not been added to the database yet.
     */
    public static Category createTransientCategory () {
        Category category = new Category ();
        category.setId (-1L);
        category.setName ("");
        return category;
    }

    @Column(unique = true)
    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    public Set<Post> getPosts () {
        return posts;
    }

    public void setPosts (Set<Post> posts) {
        this.posts = posts;
    }

}
