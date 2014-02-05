package io.collap.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table (name = "users")
public class User {

    private Long id;
    private String name;

    public User () {

    }

    public User (String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue (generator = "increment")
    @GenericGenerator (name = "increment", strategy = "increment")
    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

}
