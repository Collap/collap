package io.collap.cache;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;

import java.util.*;
import java.util.logging.Logger;

// TODO: Check thread-safety!
public class InvalidatorManager implements PostDeleteEventListener, PersistEventListener {

    private static final Logger logger = Logger.getLogger (InvalidatorManager.class.getName ());

    private Map<Class<?>, List<Invalidator>> invalidatorsMap = new HashMap<> ();

    public void addInvalidator (Class<?> entityClass, Invalidator invalidator) {
        List<Invalidator> invalidators = invalidatorsMap.get (entityClass);
        if (invalidators == null) {
            invalidators = new ArrayList<> ();
            invalidatorsMap.put (entityClass, invalidators);
        }
        invalidators.add (invalidator);
    }

    public List<Invalidator> getInvalidators (Class entityClass) {
        return invalidatorsMap.get (entityClass);
    }

    public void invalidate (Object entity) {
        Class<?> cl = entity.getClass ();
        List<Invalidator> invalidators = invalidatorsMap.get (cl);
        if (invalidators != null) {
            for (Invalidator invalidator : invalidators) {
                invalidator.invalidate (entity);
            }
        }
    }

    @Override
    public void onPostDelete (PostDeleteEvent event) {
        logger.info ("onPostDelete: " + event.getEntity ().getClass ());
        invalidate (event.getEntity ());
    }

    @Override
    public void onPersist (PersistEvent event) throws HibernateException {
        logger.info ("onPersist: " + event.getEntityName ());
        invalidate (event.getObject ());
    }

    @Override
    public void onPersist (PersistEvent event, Map createdAlready) throws HibernateException {
        logger.info ("onPersist: " + event.getEntityName ());
        invalidate (event.getObject ());
    }

    @Override
    public boolean requiresPostCommitHanding (EntityPersister persister) {
        return false;
    }

}
