package com.knaptus.domainiser.core;

import java.util.Map;
import java.util.Set;

/**
 * Defines a domain graph with all its children graphs.
 *
 * @author Aditya Bhardwaj
 * @since 0.0.1
 */
public interface DomainGraphDefinition<K> {

    /**
     * Returns the domain definition.
     *
     * @return
     */
    DomainDefinition<K> getDomainDefinition();

    /**
     * Return the Class of the domain definition.
     *
     * @return the domainClass
     */
    Class<K> getDomainClass();

    /**
     * Returns all the children property names.
     *
     * @return
     */
    Set<String> getAllChildrenNames();

    /**
     * Adds child domain graph.
     *
     * @param property
     * @param child
     */
    void addChild(String property, DomainGraphDefinition<?> child);

    /**
     * Adds child domain graph with default domain graph
     *
     * @param property
     * @param propertyDomainDefinition
     */
    void addChild(String property, DomainDefinition<?> propertyDomainDefinition);

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    <T> DomainGraphDefinition<T> getChild(String property, Class<T> clazz);

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    DomainGraphDefinition<?> getChild(String property);


    /**
     * Returns user friendly name for the graph.
     *
     * @return
     */
    String getName();

    /**
     * Returns the Map representation.
     *
     * @return
     */
    Map getGraph();
}
