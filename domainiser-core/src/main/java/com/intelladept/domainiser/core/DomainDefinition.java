package com.intelladept.domainiser.core;

import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.reflect.FastClass;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.lang.model.element.TypeParameterElement;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

/**
 * Holds the RW properties for a domain Model and also provides information about the nested
 * domain models.
 *
 * @author Addy
 * @version $Id $
 */
public class DomainDefinition<K> implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainDefinition.class);

    private final Class<K> clazz;

    private final Map<String, PropertyDefinition> properties;
    
    private DomainDefinition(Class<K> clazz) {
        this.clazz = clazz;
        properties = new HashMap<String, PropertyDefinition>();
    }


    /**
     * Provides instance of domain definition for the provided domain resolver.
     *
     * @param clazz
     * @param domainResolver
     * @param <T>
     * @return
     */
    public static <T> DomainDefinition<T> getInstance(Class<T> clazz, DomainResolver domainResolver) {
        DomainDefinition<T> domainDefinition = new DomainDefinition<T>(clazz);
        domainDefinition.init(domainResolver);
        return domainDefinition;
    }

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
    
    public Set<String> getProperties() {
        if (properties.size() == 0) {
            LOGGER.warn("No properties found. Check if Domain Definition was initialised using Domain Resolver");
        }
        return properties.keySet();
    }

    public Class<?> getUnderlyingDomainModel(String property) {
        PropertyDefinition propertyDefinition = properties.get(property);
        if (propertyDefinition != null) {
            return propertyDefinition.domainClass;
        }
        return null;
    }
    
    public Class<?> getActualClass(String property) {
        PropertyDefinition propertyDefinition = properties.get(property);
        if (propertyDefinition != null) {
            return propertyDefinition.actualClass;
        }
        return null;
    }

    public Class<K> getClazz() {
        return clazz;
    }

    /**
     * Holds the property specific information.
     */
    private static class PropertyDefinition {
        private Class<?> actualClass;
        private Class<?> domainClass;

        public PropertyDefinition(Class<?> actualClass, Class<?> domainClass) {
            this.actualClass = actualClass;
            this.domainClass = domainClass;
        }
    }
}
