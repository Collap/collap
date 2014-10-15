package io.collap.std.user.entity;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "users")
public class User extends io.collap.entity.Entity {

    private String username;
    private String passwordHash;

    public User () {

    }

    public User (String username) {
        this.username = username;
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
