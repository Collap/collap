package io.collap.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Entity {

    protected Long id;

    @Id
    @GeneratedValue(generator = "auto_id")
    @GenericGenerator(name = "auto_id", strategy = "native")
    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    @Override
    public int hashCode () {
        return id.hashCode ();
    }

    @Override
    public boolean equals (Object obj) {
        if (obj instanceof Entity) {
            return id.equals (((Entity) obj).getId ());
        }
        return false;
    }

}
