package com.intelladept.domainiser.clone;

import com.intelladept.domainiser.core.AbstractDomainWalker;
import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

/**
 * Clones domain model based on the domain graph definition and domain definition provided.
 * <p>
 *     TODO Improvements
 *     <ul>
 *         <li>Logs should should the graph being operated in dot notation</li>
 *         <li>Test for bi-directional relationships if they are preserved</li>
 *         <li>Test if an object already cloned is used as reference or is duplicated</li>
 *     </ul>
 * </p>
 */
public class CloningDomainWalker extends AbstractDomainWalker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloningDomainWalker.class);

    // domain objects which are not cloned, can be left as original objects or set to null
    private boolean keepReferences = false;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T walk(T domainModel, DomainGraphDefinition<T> domainGraphDefinition) {
        LOGGER.debug("Domain cloning started for [{}]; domain graph def was for class [{}]", domainModel, domainGraphDefinition.getDomainClass());

        Validate.notNull(domainModel, "Domain model to be cloned cannot be null");
        Validate.notNull(domainGraphDefinition, "Domain graph definition cannot be null");
        Validate.isTrue(domainGraphDefinition.getDomainClass().equals(domainModel.getClass()), "Domain model and graph definition passed do not match");

        T clonedModel = null;
        try {
            Map<String, Object> domainMap = BeanMap.create(domainModel);
            BeanMap clonedDomainBeanMap = BeanMap.create(domainModel.getClass().newInstance());
            DomainDefinition<T> domainDefinition = domainGraphDefinition.getDomainDefinition();

            for (String propertyName : domainDefinition.getProperties()) {
                Class<?> propertyClass = domainDefinition.getUnderlyingDomainModel(propertyName);

                //if property class is set then it is a domain object otherwise it should be just copied
                if (propertyClass != null) {
                    DomainGraphDefinition childDef = domainGraphDefinition.getChild(propertyName);
                    Class<?> actualPropertyClass = domainDefinition.getActualClass(propertyName);

                    Object propertyValue = null;
                    //walk the child tree if the child def was found
                    if (childDef != null) {
                        if (List.class.isAssignableFrom(actualPropertyClass)) {
                            propertyValue = walkList((List<?>) domainMap.get(propertyName), childDef);
                        } else if (Set.class.isAssignableFrom(actualPropertyClass)) {
                            propertyValue = walkSet((Set<?>) domainMap.get(propertyName), childDef);
                        } else if (Map.class.isAssignableFrom(actualPropertyClass)) {
                            propertyValue = walkMap((Map<?, ?>) domainMap.get(propertyName), childDef);
                        } else {
                            propertyValue = walk(domainMap.get(propertyName), childDef);
                        }
                        LOGGER.debug("Property [{}] cloned and set to [{}]", propertyName, propertyValue);
                        clonedDomainBeanMap.put(propertyName, propertyValue);
                    } else if (this.keepReferences) {
                        LOGGER.debug("Property [{}] cloned as original for original object [{}]; property type was [{}]",
                                new Object[]{propertyName, domainModel, actualPropertyClass.getSimpleName()});
                        clonedDomainBeanMap.put(propertyName, domainMap.get(propertyName));
                    } else {
                        LOGGER.debug("Property [{}] not cloned for original object [{}]; property type was [{}]", 
                                new Object[] {propertyName, domainModel, actualPropertyClass.getSimpleName()});
                    }
                } else {
                    LOGGER.debug("Simple property [{}] copied was [{}]", propertyName, domainMap.get(propertyName));
                    clonedDomainBeanMap.put(propertyName, domainMap.get(propertyName));
                }
            }

            clonedModel = (T) clonedDomainBeanMap.getBean();
        } catch (InstantiationException e) {
            LOGGER.error("Domain cannot be cloned [{}]", domainModel);
            throw new IllegalArgumentException("Cannot instantiate the domain using default constructor", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Domain cannot be cloned [{}]", domainModel);
            throw new IllegalArgumentException("Cannot access the domain", e);
        }

        return clonedModel;

    }

    @Override
    public <K, V> Map<K, V> walkMap(Map<K, V> domainModels, Map<K, V> returnMap,
                                    DomainGraphDefinition<V> domainGraphDefinition) {
        if (domainModels != null && returnMap != null) {

            // keep local cache for cloned objects
            Map<V, V> localCache = createCache();
            for (Entry<K, V> domainEntry : domainModels.entrySet()) {

                // check local cache first
                V domainModel = domainEntry.getValue();
                V clonedModel = localCache.get(domainModel);
                if (clonedModel == null) {
                    clonedModel = walk(domainModel, domainGraphDefinition);
                    localCache.put(domainModel, clonedModel);
                }
                returnMap.put(domainEntry.getKey(), clonedModel);
            }
        }
        return returnMap;
    }

    @Override
    public <T, Z extends Collection<T>> Z walk(Collection<T> domainModels, Z returnCollection,
                                               DomainGraphDefinition<T> domainGraphDefinition) {
        if (domainModels != null) {

            Validate.notNull(returnCollection, "Collection object cannot be null");

            // keep local cache for cloned objects
            Map<T, T> localCache = createCache();
            for (T domainModel : domainModels) {

                // check local cache first
                T clonedModel = localCache.get(domainModel);
                if (clonedModel == null) {
                    clonedModel = walk(domainModel, domainGraphDefinition);
                    localCache.put(domainModel, clonedModel);
                }
                returnCollection.add(clonedModel);
            }
        }
        return returnCollection;
    }

    private <T> Map<T, T> createCache() {
        // TODO find a better data structure such that map algorithm uses object reference to find keys not equals
        // methods. That will make lookup much faster.
        return new HashMap<T, T>();
    }


    public void setKeepReferences(boolean keepReferences) {
        this.keepReferences = keepReferences;
    }
}
