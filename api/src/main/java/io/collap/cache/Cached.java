package io.collap.cache;

public interface Cached {

    public boolean shouldResponseBeCached ();

    public String getElementKey ();

}
