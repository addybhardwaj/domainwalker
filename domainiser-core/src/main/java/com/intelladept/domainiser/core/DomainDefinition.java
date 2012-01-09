package com.intelladept.domainiser.core;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Holds the RW properties for a domain Model and also provides information about the nested
 * domain models.
 *
 * @author Aditya Bhardwaj
 * @version $Id $
 */
public final class DomainDefinition<K> implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainDefinition.class);
    public static final String UNINITIALISED_ERROR_MESSAGE = "DomainDefinition has not been initiliased with a DomainResolver";

    private final Class<K> clazz;

    private final Map<String, PropertyDefinition> properties;

    private boolean isInitialised = false;
    
    private DomainDefinition(Class<K> clazz) {
        this.clazz = clazz;
        properties = new HashMap<String, PropertyDefinition>();
    }

    private static Lock domainResolverCacheLock = new ReentrantLock();
    private static Lock classDefinitionCacheLock = new ReentrantLock();

    private static final Map<DomainResolver, Map<Class<?>, DomainDefinition<?>>> CACHED_DEFINITIONS =
            new WeakHashMap<DomainResolver, Map<Class<?>, DomainDefinition<?>>>();


    /**
     * Provides instance of domain definition for the provided domain resolver.
     *
     * @param clazz
     * @param domainResolver
     * @param <T>
     * @return
     */
    public static <T> DomainDefinition<T> getInstance(Class<T> clazz, DomainResolver domainResolver) {
        if (CACHED_DEFINITIONS.get(domainResolver) == null) {
            domainResolverCacheLock.lock();
            try {
                if (CACHED_DEFINITIONS.get(domainResolver) == null) {
                   CACHED_DEFINITIONS.put(domainResolver, new WeakHashMap<Class<?>, DomainDefinition<?>>());
                }
            } finally {
                domainResolverCacheLock.unlock();
            }
        }

        Map<Class<?>, DomainDefinition<?>> cachedClassDef =  CACHED_DEFINITIONS.get(domainResolver);

        if(cachedClassDef.get(clazz) == null) {
            classDefinitionCacheLock.lock();

            try {
                if(cachedClassDef.get(clazz) == null) {
                    DomainDefinition<T> domainDefinition = new DomainDefinition<T>(clazz);
                    domainDefinition.init(domainResolver);
                    cachedClassDef.put(clazz, domainDefinition);
                }
            } finally {
                classDefinitionCacheLock.unlock();
            }
        }

        return (DomainDefinition<T>) cachedClassDef.get(clazz);
    }

    /**
     * Initialises the domain definition with the properties and its types. This is critical for the functioning
     * of utilities using this class.
     *
     * @param domainResolver
     */
    public void init(DomainResolver domainResolver) {
        Validate.notNull(domainResolver, "Domain resolver cannot be null");

        LOGGER.info("Initialising Domain Definition for [{}]", getClazz());
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
        if (propertyDescriptors != null) {

            for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {

                //process only readable and writable properties
                if (propertyDescriptor.getReadMethod() != null
                        && propertyDescriptor.getWriteMethod() != null) {

                    Type type = propertyDescriptor.getReadMethod().getGenericReturnType();
                    Class rawType = propertyDescriptor.getPropertyType();

                    Class<?> domainClass = rawType; //default value
                    boolean isUnknownType = false;

                    if (type instanceof Class) {
                        domainClass = (Class) type;
                    } else if (type instanceof ParameterizedType) {
                        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        int underlyingTypeIndex = getUnderlyingTypeIndex(rawType);
                        //extract underlying type
                        if(actualTypeArguments.length > underlyingTypeIndex) {
                            Type actualTypeArgument = actualTypeArguments[underlyingTypeIndex];
                            if (actualTypeArgument instanceof Class) {
                                domainClass = (Class<?>) actualTypeArgument;
                            } else if (actualTypeArgument instanceof WildcardType) {
                                WildcardType wildcardType = (WildcardType) actualTypeArgument;
                                if (wildcardType.getLowerBounds() != null
                                        && wildcardType.getLowerBounds().length == 1
                                        && wildcardType.getLowerBounds()[0] instanceof Class) {
                                    domainClass = (Class<?>) wildcardType.getLowerBounds()[0];
                                } else if (wildcardType.getUpperBounds() != null
                                        && wildcardType.getUpperBounds().length == 1
                                        && wildcardType.getUpperBounds()[0] instanceof Class) {
                                    domainClass = (Class<?>) wildcardType.getUpperBounds()[0];
                                } else {
                                    isUnknownType = true;
                                }
                            } else {
                                isUnknownType = true;
                            }

                        } else {
                            isUnknownType = true;
                        }
                        
                    } else if (type instanceof WildcardType) {
                        isUnknownType = true;

                    } else if (type instanceof GenericArrayType) {
                        isUnknownType = true;
                    }

                    if (domainClass!= null && domainResolver.isDomainModel(domainClass)) {
                        properties.put(propertyDescriptor.getName(), new PropertyDefinition(propertyDescriptor.getPropertyType(), domainClass));
                    } else if (isUnknownType || domainClass.equals(Object.class)) {
                        LOGGER.warn("Unknown property type [{}]; for method [{}]", type, propertyDescriptor.getReadMethod());
                        properties.put(propertyDescriptor.getName(), new PropertyDefinition(propertyDescriptor.getPropertyType(), domainClass));
                    } else {
                        properties.put(propertyDescriptor.getName(), new PropertyDefinition(propertyDescriptor.getPropertyType(), null));
                    }
                }
            }
        }

        isInitialised = true;
    }
    
    private int getUnderlyingTypeIndex(Class wrappingClass) {
        if(Collection.class.isAssignableFrom(wrappingClass)) {
            return 0;
            
        } else if (Map.class.isAssignableFrom(wrappingClass)) {
            return 1;
        } else {
            LOGGER.error("Unknown wrapping class [{}]", wrappingClass);
            throw new IllegalArgumentException("Unknown wrapping class type encountered");
        }
    }

    /**
     * Returns all the read-write properties.
     *
     * @return
     */
    public Set<String> getProperties() {
        Validate.isTrue(isInitialised, UNINITIALISED_ERROR_MESSAGE);
        if (properties.size() == 0) {
            LOGGER.warn("No properties found. Check if Domain Definition was initialised using Domain Resolver");
        }
        return properties.keySet();
    }

    /**
     * Returns the Class type of the provided property, if the property is a domain object. Otherwise null is returned.
     *
     * @param property
     * @return
     */
    public Class<?> getUnderlyingDomainModel(String property) {
        Validate.isTrue(isInitialised, UNINITIALISED_ERROR_MESSAGE);
        PropertyDefinition propertyDefinition = properties.get(property);
        if (propertyDefinition != null) {
            return propertyDefinition.domainClass;
        }
        return null;
    }

    /**
     * Returns the actual Class of the property i.e. List, Map or specific class if property is an association.
     *
     * @param property
     * @return
     */
    public Class<?> getActualClass(String property) {
        Validate.isTrue(isInitialised, UNINITIALISED_ERROR_MESSAGE);
        PropertyDefinition propertyDefinition = properties.get(property);
        if (propertyDefinition != null) {
            return propertyDefinition.actualClass;
        }
        return null;
    }

    /**
     * Returns the domain object class of the domain definition.
     *
     * @return
     */
    public Class<K> getClazz() {
        return clazz;
    }

    /**
     * Holds the property specific information.
     */
    private static final class PropertyDefinition {
        private Class<?> actualClass;
        private Class<?> domainClass;

        private PropertyDefinition(Class<?> actualClass, Class<?> domainClass) {
            this.actualClass = actualClass;
            this.domainClass = domainClass;
        }
    }
}
