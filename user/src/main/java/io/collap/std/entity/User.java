package io.collap.std.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table (name = "users")
public class User {

    private Long id;
    private String username;

    private String passwordHash;

    public User () {

    }

    public User (String username) {
        this.username = username;
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

    @Column(unique = true)
    public String getUsername () {
        return username;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getPasswordHash () {
        return passwordHash;
    }

    public void setPasswordHash (String passwordHash) {
        this.passwordHash = passwordHash;
    }

}
