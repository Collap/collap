package io.collap.std.post.entity;

import io.collap.std.user.entity.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    /**
     * An ID of -1 indicates a post that has not been added to the database yet.
     */
    private Long id;
    private User author; // TODO: Allow multiple authors? After all, it's called COLLABORATION.
    private Set<Category> categories;
    private Date publishingDate;
    private Date lastEdit;
    private String title;
    private String content;
    private String compiledContent;

    public Post () {

    }

    /**
     * Creates an empty post that has not been added to the database yet and is associated with no authors.
     */
    public static Post createTransientPost () {
        Post post = new Post ();
        post.setId (-1L);
        post.setCategories (new HashSet<Category> ());
        post.setTitle ("");
        post.setContent ("");
        return post;
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

    // TODO: Eager loading to improve performance?
    // TODO: The author may be nonexistent (After account deletion for example).
    //       Use an "unknown" dummy object instead, so posts are still viewable.
    @ManyToOne(optional = false)
    @JoinColumn(name = "authorId")
    public User getAuthor () {
        return author;
    }

    public void setAuthor (User author) {
        this.author = author;
    }

    @ManyToMany /* Note: No cascade here. Categories are saved separately. */
    @JoinTable(name = "post_category_links")
    public Set<Category> getCategories () {
        return categories;
    }

    public void setCategories (Set<Category> categories) {
        this.categories = categories;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public Date getPublishingDate () {
        return publishingDate;
    }

    public void setPublishingDate (Date publishingDate) {
        this.publishingDate = publishingDate;
    }

    public Date getLastEdit () {
        return lastEdit;
    }

    public void setLastEdit (Date lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public String getCompiledContent () {
        return compiledContent;
    }

    public void setCompiledContent (String compiledContent) {
        this.compiledContent = compiledContent;
    }

}
