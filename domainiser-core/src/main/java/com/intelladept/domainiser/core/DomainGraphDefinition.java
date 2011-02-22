package com.intelladept.domainiser.core;

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

    private final Class<K> klass;

    private final Map<String, DomainGraphDefinition<?>> children;

    /**
     * Default Constructor.
     */
    public DomainGraphDefinition(Class<K> klass) {
        this.children = new HashMap<String, DomainGraphDefinition<?>>();
        this.klass = klass;
    }

    /**
     * Return the Class of the domain definition.
     * 
     * @return the klass
     */
    public Class<K> getKlass() {
        return this.klass;
    }

    /**
     * Return the children.
     * 
     * @return the children
     */
    public Map<String, DomainGraphDefinition<?>> getChildren() {
        return this.children;
    }

    /**
     * Adds child domain graph.
     * 
     * @param child
     */
    public void addChild(String property, DomainGraphDefinition<?> child) {
        getChildren().put(property, child);
    }

    /**
     * Finds a child domain graph and returns it.
     * 
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> DomainGraphDefinition<T> findDomainGraphDefinition(String property, Class<T> clazz) {
        DomainGraphDefinition<?> def = (getChildren() == null ? null : getChildren().get(
                property));

        if (def.getKlass().equals(clazz)) {
            return (DomainGraphDefinition<T>) def;
        }
        return null;
    }

}
