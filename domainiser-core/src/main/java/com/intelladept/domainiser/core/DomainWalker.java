package com.intelladept.domainiser.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides capability to traverse a specific section of domain graph and take action on the traversed path. For
 * instance, implementation of DomainWalker can be used to clone a section of domain graph or lazily load domain graph
 * in conjunction with ORM libraries.
 * 
 * @author Aditya Bhardwaj
 * @version $Id $
 */
public interface DomainWalker {

    /**
     * Walks the domain model with default domain graph definition.
     *
     * @param domainModel
     * @param <T>
     * @return
     */
    <T> T walk(T domainModel);

    /**
     * Walks the domain model with the provided domain graph definition.
     *
     * @param domainModel
     * @param domainGraphDefinition
     * @param <T>
     * @return
     */
    <T> T walk(T domainModel, DomainGraphDefinition<T> domainGraphDefinition);

    /**
     * Walks a list of domain objects with the default domain graph def.
     * 
     * @param domainModels
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> walkList(List<T> domainModels, Class<T> clazz);

    /**
     * Walks a list of domain objects with the provided domain graph def.
     * @param domainModels
     * @param domainGraphDefinition
     * @param <T>
     * @return
     */
    <T> List<T> walkList(List<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition);

    /**
     * Walks a set of domain objects with the default domain graph def.
     * 
     * @param domainModels
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Set<T> walkSet(Set<T> domainModels, Class<T> clazz);

    /**
     * Walks a set of domain objects with the provided domain graph def.
     * 
     * @param domainModels
     * @param domainGraphDefinition
     * @param <T>
     * @return
     */
    <T> Set<T> walkSet(Set<T> domainModels, DomainGraphDefinition<T> domainGraphDefinition);

    /**
     * Walks a map of domain objects (domain objects set as the values) with the provided domain graph def.
     * 
     * @param domainModels
     * @param domainGraphDefinition
     * @param <K> non domain key types
     * @param <V> domain objects
     * @return
     */
    <K, V> Map<K, V> walkMap(Map<K, V> domainModels, DomainGraphDefinition<V> domainGraphDefinition);

    /**
     * Walks a map of domain objects (domain objects set as values) with default domain graph def.
     *
     * @param domainModels
     * @param clazz
     * @param <K> non domain key types
     * @param <V> domain objects
     * @return
     */
    <K, V> Map<K, V> walkMap(Map<K, V> domainModels, Class<V> clazz);

}
