package com.intelladept.domainiser.core;

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
public interface DomainWalker {

    <T> T walk(T domainModel);

    <T> T walk(T domainModel, DomainGraphDefinition domainGraphDefinition);

    <T> List<T> walk(List<T> domainModels);

    <T> List<T> walk(List<T> domainModels, DomainGraphDefinition domainGraphDefinition);

    <T> Set<T> walk(Set<T> domainModels);

    <T> Set<T> walk(Set<T> domainModels, DomainGraphDefinition domainGraphDefinition);

    <K, V> Map<K, V> walk(Map<K, V> domainModels);

    <K, V> Map<K, V> walk(Map<K, V> domainModels, Map<K, V> returnMap,
            DomainGraphDefinition domainGraphDefinition);

}
