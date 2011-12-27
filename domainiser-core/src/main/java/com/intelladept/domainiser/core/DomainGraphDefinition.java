package com.intelladept.domainiser.core;

import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines a domain graph for a specific domain model. This definition is utilised by domain walkers to hydrate domain
 * models with data before the service layer transactions are closed. <br/> A domain model can have many domain graph
 * definitions because various consumers of services might require different domain depths.
 *
 * @author Addy
 * @version $Id $
 */
public class DomainGraphDefinition<K> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, DomainGraphDefinition<?>> children;

    private final DomainDefinition<K> domainDefinition;


    /**
     * Default Constructor.
     */
    public DomainGraphDefinition(DomainDefinition<K> domainDefinition) {
        this.children = new HashMap<String, DomainGraphDefinition<?>>();
        this.domainDefinition = domainDefinition;
    }

    /**
     * Returns the domain definition.
     *
     * @return
     */
    public DomainDefinition<K> getDomainDefinition() {
        return domainDefinition;
    }

    /**
     * Return the Class of the domain definition.
     *
     * @return the domainClass
     */
    public Class<K> getDomainClass() {
        return this.domainDefinition.getClazz();
    }

    /**
     * Return the children.
     *
     * @return the children
     */
    public Map<String, DomainGraphDefinition<?>> getAllChildren() {
        return this.children;
    }

    /**
     * Adds child domain graph.
     *
     * @param property
     * @param child
     */
    public void addChild(String property, DomainGraphDefinition<?> child) {
        //check if this property exist
        Validate.isTrue(this.domainDefinition.getProperties().contains(property), property + " :property doesn't exist in the domain: " + getDomainClass());
        getAllChildren().put(property, child);
    }

    /**
     * Adds child domain graph with default domain graph
     *
     * @param property
     * @param propertyDomainDefinition
     */
    public void addChild(String property, DomainDefinition<?> propertyDomainDefinition) {
        //check if this property exist
        Validate.isTrue(this.domainDefinition.getProperties().contains(property), property + " :property doesn't exist in the domain: " + getDomainClass());
        getAllChildren().put(property, new DomainGraphDefinition(propertyDomainDefinition));
    }

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> DomainGraphDefinition<T> getChild(String property, Class<T> clazz) {
        DomainGraphDefinition<?> def = getChild(property);

        if (def != null && clazz.isAssignableFrom(def.getDomainClass())) {
            return (DomainGraphDefinition<T>) def;
        }
        return null;
    }

    /**
     * Finds a child domain graph and returns it.
     *
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    public DomainGraphDefinition<?> getChild(String property) {
        DomainGraphDefinition<?> def = this.children.get(property);

        return def;
    }

}
