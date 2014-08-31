package io.collap.entity;

import org.hibernate.HibernateException;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This user type is globally known as PersistentEnum.
 * You need to supply a parameter called "type", which must be the name of the enum class.
 */
public class PersistentEnumUserType implements UserType, ParameterizedType {

    private static final Logger logger = Logger.getLogger (PersistentEnumUserType.class.getName ());

    private Class<? extends PersistentEnum> enumClass;

    @Override
    public void setParameterValues (Properties parameters) {
        String className = parameters.getProperty ("type");
        try {
            enumClass = (Class<? extends PersistentEnum>) Class.forName (className);
        } catch (ClassNotFoundException e) {
            String msg = "Enum class '" + className + "' could not be found.";
            logger.log (Level.SEVERE, msg, e);
            throw new IllegalArgumentException (msg, e);
        }
    }

    @Override
    public int[] sqlTypes () {
        return new int[] {
                Types.INTEGER
        };
    }

    @Override
    public Class returnedClass () {
        return enumClass;
    }

    @Override
    public boolean equals (Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode (Object x) throws HibernateException {
        if (x == null) {
            return 0;
        }

        return x.hashCode ();
    }

    @Override
    public Object nullSafeGet (ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {

        int id = rs.getInt (names[0]);
        if (rs.wasNull ()) {
            return null;
        }

        for (PersistentEnum value : enumClass.getEnumConstants ()) {
            if (id == value.getId ()) {
                return value;
            }
        }

        throw new RuntimeException ("Unknown id '" + id + "' in enum " + enumClass.getSimpleName ());
    }

    @Override
    public void nullSafeSet (PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {

    }

    @Override
    public Object deepCopy (Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable () {
        return false;
    }

    @Override
    public Serializable disassemble (Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble (Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace (Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

}
