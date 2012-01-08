package com.intelladept.domainiser.core.impl;

import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;

import java.util.Map;
import java.util.Set;

/**
 * Decorates the {@link com.intelladept.domainiser.core.DomainGraphDefinition} with walking path
 * <p>For instance, <br/>
 * e.g. object.spouse.children.friends etc.
 * </p>
 *
 * @author Aditya Bhardwaj
 * @since 0.0.1
 */
public class DomainGraphDefinitionDecorator<K> implements DomainGraphDefinition<K> {

    private DomainGraphDefinition<K> underlyingDomainGraphDefinition;

    private StringBuilder walkingPath;

    /**
     * Constructor to wrap top level domain graph definition.
     *
     * @param underlyingDomainGraphDefinition
     */
    public DomainGraphDefinitionDecorator(DomainGraphDefinition<K> underlyingDomainGraphDefinition) {
        this.underlyingDomainGraphDefinition = underlyingDomainGraphDefinition;
        this.walkingPath = new StringBuilder("root");
    }

    /**
     * Constructor to wrap child level domain graph definition.
     *
     * @param underlyingDomainGraphDefinition
     * @param walkingPath
     * @param property
     */
    public DomainGraphDefinitionDecorator(DomainGraphDefinition<K> underlyingDomainGraphDefinition, StringBuilder walkingPath, String property) {
        this.underlyingDomainGraphDefinition = underlyingDomainGraphDefinition;
        this.walkingPath = new StringBuilder(walkingPath).append(".").append(property);
    }

    @Override
    public DomainDefinition<K> getDomainDefinition() {
        return underlyingDomainGraphDefinition.getDomainDefinition();
    }

    @Override
    public Class<K> getDomainClass() {
        return underlyingDomainGraphDefinition.getDomainClass();
    }

    @Override
    public Set<String> getAllChildrenNames() {
        return underlyingDomainGraphDefinition.getAllChildrenNames();
    }


    @Override
    public <T> DomainGraphDefinition<T> getChild(String property, Class<T> clazz) {
        DomainGraphDefinition<T> child = underlyingDomainGraphDefinition.getChild(property, clazz);
        if  (child != null) {
            return new DomainGraphDefinitionDecorator<T>(child, walkingPath, property);
        } else {
            return null;
        }
    }

    @Override
    public DomainGraphDefinition<?> getChild(String property) {
        DomainGraphDefinition<?> decoratedChild = null;
        DomainGraphDefinition<?> child = underlyingDomainGraphDefinition.getChild(property);
        if (child != null) {
            decoratedChild = new DomainGraphDefinitionDecorator(child, walkingPath, property);
        }
        return decoratedChild;
    }

    @Override
    public String getName() {
        return walkingPath.toString();
    }

    @Override
    public Map getGraph() {
        return this.underlyingDomainGraphDefinition.getGraph();
    }

    @Override
    public void addChild(String property, DomainGraphDefinition<?> child) {
        underlyingDomainGraphDefinition.addChild(property, child);
    }

    @Override
    public void addChild(String property, DomainDefinition<?> propertyDomainDefinition) {
        underlyingDomainGraphDefinition.addChild(property, propertyDomainDefinition);
    }


}
