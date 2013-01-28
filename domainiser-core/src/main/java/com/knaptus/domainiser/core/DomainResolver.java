package com.knaptus.domainiser.core;

/**
 * Provides logic to identify Java Beans which are identified as domain objects.
 *
 * @author Aditya Bhardwaj
 * @since 0.0.1
 */
public interface DomainResolver {

    /**
     * Returns true is the class is domain object java bean.
     *
     * @param domain
     * @return
     */
    boolean isDomainModel(Class domain);
}
