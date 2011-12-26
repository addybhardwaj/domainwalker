package com.intelladept.domainiser.clone;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import com.intelladept.domainiser.core.AbstractDomainWalker;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.core.DomainResolver;

public class CloningDomainWalker extends AbstractDomainWalker {

    private DomainResolver domainResolver;

    // domain objects which are not cloned, can be left as original objects or set to null
    private boolean keepReferences = false;

    @SuppressWarnings("unchecked")
    public <T> T walk(T domainModel, DomainGraphDefinition<T> domainGraphDefinition) {

        T clonedModel = null;
        if (domainModel != null) {
            try {
                clonedModel = (T) domainModel.getClass().newInstance();
                Map<String, Object> clonedMap = BeanUtils.describe(domainModel);
                for (Entry<String, Object> property : clonedMap.entrySet()) {
                    if (this.domainResolver.isDomainModel(property.getKey())) {
                        DomainGraphDefinition<Object> childDef = null;

                        if (domainGraphDefinition != null) {
                            childDef = domainGraphDefinition.getChild(property
                                    .getKey(), Object.class);
                        }

                        Object propertyClone = null;
                        // if property needs to be cloned
                        if (childDef != null) {
                            propertyClone = walk(property.getValue(), childDef);
                        } else if (this.keepReferences) {
                            propertyClone = property.getValue();
                        }
                        clonedMap.put(property.getKey(), propertyClone);

                    }
                }

                BeanUtils.copyProperties(clonedModel, clonedMap);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return clonedModel;
    }

    public <K, V> Map<K, V> walkMap(Map<K, V> domainModels, Map<K, V> returnMap,
                                    DomainGraphDefinition<V> domainGraphDefinition) {
        if (domainModels != null) {

            // use hash map if map not provided
            if (returnMap == null) {
                returnMap = new HashMap<K, V>();
            }

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

    public DomainResolver getDomainResolver() {
        return domainResolver;
    }

    public void setDomainResolver(DomainResolver domainResolver) {
        this.domainResolver = domainResolver;
    }

    public boolean isKeepReferences() {
        return keepReferences;
    }

    public void setKeepReferences(boolean keepReferences) {
        this.keepReferences = keepReferences;
    }
}
