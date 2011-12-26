package com.intelladept.domainiser.core;

import org.apache.commons.beanutils.BeanUtils;
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
 * @author Addy
 * @version $Id $
 */
public class DomainGraphDefinition<K> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Class<K> domainClass;

    private final Map<String, DomainGraphDefinition<?>> children;

    private final Set<String> properties;

    /**
     * Default Constructor.
     */
    public DomainGraphDefinition(Class<K> domainClass) throws Exception {
        this.children = new HashMap<String, DomainGraphDefinition<?>>();
        this.domainClass = domainClass;
        K object = domainClass.newInstance();
        this.properties = BeanUtils.describe(object).keySet();
    }

    /**
     * Return the Class of the domain definition.
     *
     * @return the domainClass
     */
    public Class<K> getDomainClass() {
        return this.domainClass;
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
     * @param child
     */
    public void addChild(String property, DomainGraphDefinition<?> child) {
        //check if this property exist
        Validate.isTrue(this.properties.contains(property), property + " :property doesn't exist in the domain: " + domainClass);
        getAllChildren().put(property, child);
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

        if (def!= null && def.getDomainClass().equals(clazz)) {
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
