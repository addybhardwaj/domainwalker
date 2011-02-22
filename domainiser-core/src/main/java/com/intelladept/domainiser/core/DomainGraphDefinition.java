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
public class DomainGraphDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String domain;

    private final Map<String, DomainGraphDefinition> children;

    public DomainGraphDefinition(String domain) {
        this.domain = domain;
        this.children = new HashMap<String, DomainGraphDefinition>();
    }

    /**
     * Return the domain.
     * 
     * @return the domain
     */
    public String getDomain() {
        return this.domain;
    }

    /**
     * Return the children.
     * 
     * @return the children
     */
    public Map<String, DomainGraphDefinition> getChildren() {
        return this.children;
    }

    /**
     * Adds child domain graph.
     * 
     * @param child
     */
    public void addChild(DomainGraphDefinition child) {
        getChildren().put(child.getDomain(), child);
    }

    /**
     * Finds a child domain graph and returns it.
     * 
     * @param property
     * @return
     */
    public DomainGraphDefinition findDomainGraphDefinition(String property) {
        return (getChildren() == null ? null : getChildren().get(property));
    }

}
