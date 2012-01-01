package com.intelladept.domainiser.core.impl;

import com.intelladept.domainiser.core.*;
import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * Provides capability to traverse a specific section of domain graph and take action on the traversed path. For
 * instance, implementation of DomainWalker can be used to clone a section of domain graph or lazily load domain graph
 * in conjunction with ORM libraries.
 * 
 * @author Aditya Bhardwaj
 * @version $Id $
 */
public abstract class AbstractDomainWalker implements DomainWalker {

    private DomainResolver domainResolver;

    /**
     * Walks a collection of domain objects and returns the result in the provided collection using the provided domain
     * graph definition.
     *
     * @param domainModels
     * @param returnCollection - return collection cannot be null
     * @param domainGraphDefinition
     * @param <T>
     * @param <Z>
     * @return
     */
    public abstract <T, Z extends Collection<T>> Z walk(Collection<T> domainModels,
            Z returnCollection,
            DomainGraphDefinition<T> domainGraphDefinition);

    /**
     * Walks a map of domain objects and returns the result in the provided map using the provided domain
     * graph definition.
     *
     * @param domainModels
     * @param returnMap - return map cannot be null
     * @param domainGraphDefinition
     * @param <K>
     * @param <V>
     * @return
     */
    public abstract <K, V> Map<K, V> walkMap(Map<K, V> domainModels, Map<K, V> returnMap,
                                    DomainGraphDefinition<V> domainGraphDefinition);
    
    @Override
    public <T> T walk(T domainModel) {
        Validate.notNull(domainModel, "Domain model to be cloned cannot be null");

        DomainGraphDefinitionDecorator<T> graphDecorator = new DomainGraphDefinitionDecorator<T>(
                new DomainGraphDefinitionImpl<T>(DomainDefinition.getInstance((Class<T>) domainModel.getClass(), domainResolver))
        );

        return walk(domainModel, graphDecorator);
    }

    @Override
    public <T> List<T> walkList(List<T> domainModels, Class<T> clazz) {
        DomainGraphDefinitionDecorator<T> graphDecorator = new DomainGraphDefinitionDecorator<T>(
                new DomainGraphDefinitionImpl<T>(DomainDefinition.getInstance(clazz, domainResolver))
        );

        return walkList(domainModels, graphDecorator);
    }

    @Override
    public <T> List<T> walkList(List<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptyList(domainModels), domainGraphDefinition);
    }

    @Override
    public <T> Set<T> walkSet(Set<T> domainModels, Class<T> clazz) {
        DomainGraphDefinitionDecorator<T> graphDecorator = new DomainGraphDefinitionDecorator<T>(
                new DomainGraphDefinitionImpl<T>(DomainDefinition.getInstance(clazz, domainResolver))
        );
        return walkSet(domainModels, graphDecorator);
    }

    @Override
    public <T> Set<T> walkSet(Set<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition) {
        return walk(domainModels, createEmptySet(domainModels), domainGraphDefinition);
    }

    @Override
    public <K, V> Map<K, V> walkMap(Map<K, V> domainModels, Class<V> clazz) {
        DomainGraphDefinitionDecorator<V> graphDecorator = new DomainGraphDefinitionDecorator<V>(
                new DomainGraphDefinitionImpl<V>(DomainDefinition.getInstance(clazz, domainResolver))
        );

        return walkMap(domainModels, createEmptyMap(domainModels), graphDecorator);
    }

    @Override
    public <K, V> Map<K, V> walkMap(Map<K, V> domainModels, DomainGraphDefinition<V> domainGraphDefinition) {
        return walkMap(domainModels, createEmptyMap(domainModels), domainGraphDefinition);
    }

    /**
     * Creates an empty set for the provided model. Default implementation returns {@link HashSet}. If it needs
     * to be optimised this method should be overriden.
     *
     * @param model
     * @param <T>
     * @return
     */
    protected <T> Set<T> createEmptySet(Set<T> model) {
        return new HashSet<T>();
    }

    /**
     * Creates an empty list for the provided model. Default implementation returns {@link ArrayList}. If it needs
     * to be optimised this method should be overriden.
     *
     * @param model
     * @param <T>
     * @return
     */
    protected <T> List<T> createEmptyList(List<T> model) {
        return new ArrayList<T>();
    }

    /**
     * Creates an empty map for the provided model. Default implementation returns {@link HashMap}. If it needs
     * to be optimised this method should be overriden.
     *
     * @param model
     * @param <K>
     * @param <V>
     * @return
     */
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
