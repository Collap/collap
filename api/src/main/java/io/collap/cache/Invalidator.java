package io.collap.cache;

import java.util.Set;

/**
 * The implementation should be thread-safe!
 *
 * The entity object and all properties should have adequate equals methods!
 */
public interface Invalidator<T extends Object> {

    public void invalidate (T entity, Set<String> changedProperties);

}
