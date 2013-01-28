package com.knaptus.domainiser.core.impl;

import com.google.gson.Gson;
import com.knaptus.domainiser.core.DomainDefinition;
import com.knaptus.domainiser.core.DomainGraphDefinition;
import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines a domain graph for a specific domain model. This definition is utilised by domain walkers to hydrate domain
 * models with data before the service layer transactions are closed. <br/> A domain model can have many domain graph
 * definitions because various consumers of services might require different domain depths.
 *
 * @author Aditya Bhardwaj
 * @version $Id $
 */
public class DomainGraphDefinitionImpl<K> implements Serializable, DomainGraphDefinition<K> {

    private static final long serialVersionUID = 1L;

    private final Map<String, DomainGraphDefinition<?>> children;

    private final DomainDefinition<K> domainDefinition;

    public static final Gson GSON = new Gson();

    /**
     * Default Constructor.
     */
    public DomainGraphDefinitionImpl(DomainDefinition<K> domainDefinition) {
        this.children = new HashMap<String, DomainGraphDefinition<?>>();
        this.domainDefinition = domainDefinition;
    }

    /**
     * Returns the domain definition.
     *
     * @return
     */
    @Override
    public DomainDefinition<K> getDomainDefinition() {
        return domainDefinition;
    }

    /**
     * Return the Class of the domain definition.
     *
     * @return the domainClass
     */
    @Override
    public Class<K> getDomainClass() {
        return this.domainDefinition.getClazz();
    }


    /**
     * Returns all the children property names.
     *
     * @return
     */
    @Override
    public Set<String> getAllChildrenNames() {
        return this.children.keySet();
    }

    /**
     * Adds child domain graph.
     *
     * @param property
     * @param child
     */
    @Override
    public void addChild(String property, DomainGraphDefinition<?> child) {
        //check if this property exist
        Validate.isTrue(this.domainDefinition.getProperties().contains(property), property + " :property doesn't exist in the domain: " + getDomainClass());
        this.children.put(property, child);
    }

    /**
     * Adds child domain graph with default domain graph
     *
     * @param property
     * @param propertyDomainDefinition
     */
    @Override
    public void addChild(String property, DomainDefinition<?> propertyDomainDefinition) {
        //check if this property exist
        Validate.isTrue(this.domainDefinition.getProperties().contains(property), property + " :property doesn't exist in the domain: " + getDomainClass());
        this.children.put(property, new DomainGraphDefinitionImpl(propertyDomainDefinition));
    }

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> DomainGraphDefinition<T> getChild(String property, Class<T> clazz) {
        DomainGraphDefinition<?> def = getChild(property);

        if (def != null && clazz.isAssignableFrom(def.getDomainClass())) {
            return (DomainGraphDefinitionImpl<T>) def;
        }
        return null;
    }

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public DomainGraphDefinition<?> getChild(String property) {
        DomainGraphDefinition<?> def = this.children.get(property);

        return def;
    }

    /**
     * Returns name of the domain class.
     *
     * @return
     */
    @Override
    public String getName() {
        return this.domainDefinition.getClazz().getSimpleName();
    }

    /**
     * Returns Map representation of the graph.
     *
     * @return
     */
    @Override
    public Map getGraph() {
        Map graph = new HashMap();
        for(String childProperty : children.keySet()) {
            graph.put(childProperty, children.get(childProperty).getGraph());
        }

        return graph;
    }


    @Override
    public String toString() {
        return GSON.toJson(getGraph());
    }


}
