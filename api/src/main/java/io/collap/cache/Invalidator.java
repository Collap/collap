package io.collap.cache;

/**
 * The implementation should be thread-safe!
 */
public interface Invalidator {

    public void invalidate (Object entity);

}
