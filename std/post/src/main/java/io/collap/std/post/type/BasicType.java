package io.collap.std.post.type;

import io.collap.Collap;
import io.collap.controller.communication.Request;
import io.collap.entity.Entity;
import io.collap.std.post.entity.Post;
import org.hibernate.Session;

import javax.annotation.Nullable;

public abstract class BasicType implements Type {

    protected Collap collap;

    protected BasicType (Collap collap) {
        this.collap = collap;
    }

    @Override
    public final Long update (@Nullable Long dataId, Request request) {
        Session session = collap.getSessionFactory ().getCurrentSession ();
        Entity data;
        if (dataId != null) {
            data = (Entity) session.get (getDataClass (), dataId);
            if (data == null) {
                throw new RuntimeException (getDataClass ().getSimpleName () + " was expected but not found for ID " + dataId + "!");
            }
        }else { /* Object has to be created! */
            try {
                data = getDataClass ().newInstance ();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace ();
                throw new RuntimeException ("Could not create new instance of data class " + getDataClass ().getSimpleName ());
            }
        }

        update (data, request);
        return (Long) session.save (data);
    }

    /**
     * Is called by update after the data is loaded or created.
     */
    protected abstract void update (Entity data, Request request);

    @Override
    public final String getEditor (@Nullable Long dataId) {
        Session session = collap.getSessionFactory ().getCurrentSession ();
        Entity data;
        if (dataId != null) {
            data = (Entity) session.get (getDataClass (), dataId);
        }else {
            data = null;
        }

        return getEditor (data);
    }

    /**
     * @param data null means the data does not exist.
     */
    protected abstract String getEditor (@Nullable Entity data);

    @Override
    public final void compile (Post post) {
        Session session = collap.getSessionFactory ().getCurrentSession ();
        Entity entity = (Entity) session.get (getDataClass (), post.getTypeDataId ());
        compile (entity, post);
    }

    protected abstract void compile (Entity entity, Post post);

    @Override
    public void deleteData (Post post) {
        Session session = collap.getSessionFactory ().getCurrentSession ();

        /* Important note: Use load to save SQL queries, only the ID is loaded! */
        Object obj = session.load (getDataClass (), post.getTypeDataId ());
        session.delete (obj);
    }

    protected abstract Class<? extends Entity> getDataClass ();

}
