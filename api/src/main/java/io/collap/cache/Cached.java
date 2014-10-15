package io.collap.cache;

/**
 * Note that fragments are only cached if the response status is HttpStatus.ok.
 */
public interface Cached {

    public boolean shouldResponseBeCached ();

    public String getElementKey ();

}
