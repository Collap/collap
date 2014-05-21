package io.collap.util;

import io.collap.Collap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * The TransactionHelper should be thread-safe.
 */
public class TransactionHelper {

    private Collap collap;

    public TransactionHelper (Collap collap) {
        this.collap = collap;
    }

    public enum TransactionMode {
        update,
        save
    }

    // TODO: 'load' methods for more standard queries.

    public <T> T load (long id, Class<?> objectClass) {
        return load (id, objectClass, "id");
    }

    public <T> T load (long id, Class<?> objectClass, String idColumnName) {
        Session session = collap.getSessionFactory ().openSession ();
        String queryString = "from " + objectClass.getSimpleName () + " as obj where obj." + idColumnName + " = ?";
        T object = (T) session.createQuery (queryString).setLong (0, id).uniqueResult ();
        session.close ();
        return object;
    }

    /**
     *
     * @return Whether the save was successful.
     */
    public boolean save (Object object) {
        return write (object, TransactionMode.save);
    }

    /**
     *
     * @return Whether the update was successful.
     */
    public boolean update (Object object) {
        return write (object, TransactionMode.update);
    }

    /**
     *
     * @return Whether the save or update was successful.
     */
    private boolean write (Object object, TransactionMode mode) {
        Session session = collap.getSessionFactory ().openSession ();
        Transaction transaction = null;
        boolean success;
        try {
            transaction = session.beginTransaction ();
            if (mode == TransactionMode.update) {
                session.update (object);
            }else { /* Mode.save. */
                session.save (object);
            }
            transaction.commit ();
            success = true;
        } catch (HibernateException ex) {
            if (transaction != null) {
                transaction.rollback ();
            }
            success = false;
        } finally {
            session.close ();
        }

        return success;
    }

}
