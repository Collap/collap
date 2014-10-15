package io.collap.std.post.entity;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "post_data_plain")
public class PlainData extends io.collap.entity.Entity{

    private String title;
    private String source;

    public PlainData () {

    }

    public static PlainData createTransientPlainData () {
        PlainData data = new PlainData ();
        data.setId (-1L);
        data.setTitle ("");
        data.setSource ("");
        return data;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    @Column(columnDefinition="mediumtext")
    public String getSource () {
        return source;
    }

    public void setSource (String source) {
        this.source = source;
    }

}
