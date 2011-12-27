package com.intelladept.domainiser.core;

import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * Provides capability to traverse a specific section of domain graph and take action on the traversed path. For
 * instance, implementation of DomainWalker can be used to clone a section of domain graph or lazily load domain graph
 * in conjunction with ORM libraries.
 * 
 * @author Addy
 * @version $Id $
 */
public abstract class AbstractDomainWalker implements DomainWalker {

    private DomainResolver domainResolver;

    public abstract <T, Z extends Collection<T>> Z walk(Collection<T> domainModels,
            Z returnCollection,
            DomainGraphDefinition<T> domainGraphDefinition);

    @Override
    public <T> T walk(T domainModel) {
        Validate.notNull(domainModel, "Domain model to be cloned cannot be null");

        return walk(domainModel, new DomainGraphDefinition<T>(DomainDefinition.getInstance((Class<T>) domainModel.getClass(), domainResolver)));
    }

    @Override
    public <T> List<T> walkList(List<T> domainModels, Class<T> clazz) {
        return walkList(domainModels, new DomainGraphDefinition<T>(DomainDefinition.getInstance(clazz, domainResolver)));
    }

    @Override
    public <T> List<T> walkList(List<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptyList(domainModels), domainGraphDefinition);
    }

    @Override
    public <T> Set<T> walkSet(Set<T> domainModels, Class<T> clazz) {
        return walkSet(domainModels, clazz);
    }

    @Override
    public <T> Set<T> walkSet(Set<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptySet(domainModels), domainGraphDefinition);
    }

    @Override
    public <K, V> Map<K, V> walkMap(Map<K, V> domainModels, DomainGraphDefinition<V> domainGraphDefinition) {
        return walkMap(domainModels, createEmptyMap(domainModels), domainGraphDefinition);
    }

    protected <T> Set<T> createEmptySet(Set<T> model) {
        return new HashSet<T>();
    }

    protected <T> List<T> createEmptyList(List<T> model) {
        return new ArrayList<T>();
    }

    protected <K, V> Map<K, V> createEmptyMap(Map<K, V> model) {
        return new HashMap<K, V>();
    }

    public DomainResolver getDomainResolver() {
        return domainResolver;
    }

    public void setDomainResolver(DomainResolver domainResolver) {
        this.domainResolver = domainResolver;
    }
}
