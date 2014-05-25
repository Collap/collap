package io.collap.std.post.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "post_categories")
public class Category {

    private Long id;
    private String name;

    public Category () {

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

}
