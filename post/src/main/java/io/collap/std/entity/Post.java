package io.collap.std.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    /* Table fields. */
    private Long id;
    private Long authorId; // TODO: Allow multiple authors? After all, it's called COLLABORATION.
    private Long categoryId;
    private Date publishingDate;
    private Date lastEdit;
    private String title;
    private String content;
    private String compiledContent;

    public Post () {

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

    public Long getAuthorId () {
        return authorId;
    }

    public void setAuthorId (Long authorId) {
        this.authorId = authorId;
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
