package com.intelladept.domainiser.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides capability to traverse a specific section of domain graph and take action on the traversed path. For
 * instance, implementation of DomainWalker can be used to clone a section of domain graph or lazily load domain graph
 * in conjunction with ORM libraries.
 * 
 * @author Addy
 * @version $Id $
 */
public abstract class AbstractDomainWalker implements DomainWalker {

    public abstract <T, Z extends Collection<T>> Z walk(Collection<T> domainModels,
            Z returnCollection,
            DomainGraphDefinition<T> domainGraphDefinition);

    public <T> T walk(T domainModel) {
        return walk(domainModel, null);
    }

    public <T> List<T> walk(List<T> domainModels) {
        return walk(domainModels, null);
    }

    public <T> List<T> walk(List<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptyList(domainModels), domainGraphDefinition);
    }

    public <T> Set<T> walk(Set<T> domainModels) {
        return walk(domainModels, null);
    }

    public <T> Set<T> walk(Set<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptySet(domainModels), domainGraphDefinition);
    }

    public <K, V> Map<K, V> walk(Map<K, V> domainModels) {
        return walk(domainModels, null);
    }

    protected <T> Set<T> createEmptySet(Set<T> model) {
        return new HashSet<T>();
    }

    protected <T> List<T> createEmptyList(List<T> model) {
        return new ArrayList<T>();
    }
}
