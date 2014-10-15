package io.collap.cache;

import io.collap.entity.Entity;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

// TODO: Check thread-safety!
public class InvalidatorManager extends EmptyInterceptor {

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

    public void invalidate (Object entity, Set<String> changedProperties) {
        Class<?> cl = entity.getClass ();
        List<Invalidator> invalidators = invalidatorsMap.get (cl);
        if (invalidators != null) {
            for (Invalidator invalidator : invalidators) {
                invalidator.invalidate (entity, changedProperties);
            }
        }
    }

    /**
     * @return Whether at least one invalidator is registered for the Entity class.
     */
    private boolean hasInvalidator (Object entity) {
        return invalidatorsMap.containsKey (entity.getClass ());
    }

    @Override
    public boolean onSave (Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (!hasInvalidator (entity)) {
            return false;
        }

        logger.info ("On save: " + entity.getClass ());

        invalidate (entity, new HashSet<> (Arrays.asList (propertyNames)));

        return false;
    }

    @Override
    public boolean onFlushDirty (Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (!hasInvalidator (entity)) {
            return false;
        }

        logger.info ("On flush dirty: " + ((Entity) entity).getId () + " [" + entity.getClass () + "]");

        int numProps = currentState.length;
        Set<String> changedProps = new HashSet<> ();
        for (int i = 0; i < numProps; ++i) {
            Object current = currentState[i];
            Object previous = previousState[i];

            if (current == null) continue;

            if (!current.equals (previous)) {
                changedProps.add (propertyNames[i]);
            }
        }

        invalidate (entity, changedProps);

        return false;
    }

    @Override
    public void onDelete (Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (!hasInvalidator (entity)) {
            return;
        }

        logger.info ("On Delete: " + ((Entity) entity).getId ());

        invalidate (entity, new HashSet<> (Arrays.asList (propertyNames)));
    }

}
