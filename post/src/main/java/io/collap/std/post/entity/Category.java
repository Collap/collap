package io.collap.std.post.entity;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "post_categories")
public class Category {

    /**
     * An ID of -1 indicates a post that has not been added to the database yet.
     */
    private Long id;
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

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
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
