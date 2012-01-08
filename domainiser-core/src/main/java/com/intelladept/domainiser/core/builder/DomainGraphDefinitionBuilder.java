package com.intelladept.domainiser.core.builder;

import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.core.DomainResolver;
import com.intelladept.domainiser.core.impl.DomainGraphDefinitionImpl;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 * @since 0.0.1
 */
public class DomainGraphDefinitionBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainGraphDefinitionBuilder.class);

    public static final String DOT_DELIMITER = "\\.";
    private DomainResolver domainResolver;
    private Class<T> clazz;
    
    private Map<String, DomainGraphDefinitionBuilder<?>> childrenBuilders = new HashMap<String, DomainGraphDefinitionBuilder<?>>();
        
    public DomainGraphDefinitionBuilder() {}
    
    public DomainGraphDefinitionBuilder(DomainResolver domainResolver, Class<T> clazz) {
        this.domainResolver = domainResolver;
        this.clazz = clazz;
    }
    
    public DomainGraphDefinitionBuilder<T> withResolver(DomainResolver domainResolver) {
        this.domainResolver = domainResolver;
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> forClass(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> withSinglePath(String... nestedPath) {
        if (nestedPath != null) {
            List<String> nestedPathList = new ArrayList<String>();
            for(String property : nestedPath) {
                nestedPathList.add(property);
            }
            withSinglePath(nestedPathList);
        }
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> withSinglePath(List<String> nestedPath) {
        if(nestedPath != null && nestedPath.size() > 0) {
            String childName = nestedPath.remove(0);
            DomainGraphDefinitionBuilder<?> childBuilder = childrenBuilders.get(childName);
            DomainDefinition<T> domainDefinition = DomainDefinition.getInstance(clazz, domainResolver);
            Class<?> childClass = domainDefinition.getUnderlyingDomainModel(childName);
            
            if(childBuilder == null) {
                childBuilder = new DomainGraphDefinitionBuilder(domainResolver, childClass);
                childrenBuilders.put(childName, childBuilder);
            }
            
            childBuilder.withSinglePath(nestedPath);
        }
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> withPathsDotNotation(String... paths) {
        if(paths != null) {
            for(String singlePathString : paths) {
                String[] singlePath = singlePathString.split(DOT_DELIMITER);
                withSinglePath(singlePath);
            }
        }
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> withDomainGraphDefinitionBuilder(DomainGraphDefinitionBuilder<?> childDomainGraphDef, String childProperty) {
        if(childDomainGraphDef != null) {
            DomainGraphDefinitionBuilder<?> childBuilder = getAndSetChildBuilder(childProperty);

            Map<String, DomainGraphDefinitionBuilder<?>> providedChildrenBuilders = childDomainGraphDef.childrenBuilders;
            if(providedChildrenBuilders != null) {
                for(String grandChild : providedChildrenBuilders.keySet()) {
                    childBuilder.withDomainGraphDefinitionBuilder(providedChildrenBuilders.get(grandChild),grandChild);
                }
            }
        }
        return this;
    }

    private DomainGraphDefinitionBuilder<?> getAndSetChildBuilder(String childProperty) {
        Class<?> childClass = DomainDefinition.getInstance(clazz, domainResolver).getUnderlyingDomainModel(childProperty);
        DomainGraphDefinitionBuilder<?> childBuilder = this.childrenBuilders.get(childProperty);

        if(childBuilder == null) {
            childBuilder = new DomainGraphDefinitionBuilder(domainResolver, childClass);
            childrenBuilders.put(childProperty, childBuilder);
        }
        return childBuilder;
    }

    public DomainGraphDefinitionBuilder<T> withDefinitionJson(String json) {
        Map def = DomainGraphDefinitionImpl.GSON.fromJson(json, Map.class);
        withDefinitionMap(def);
        return this;
    }
    
    public DomainGraphDefinitionBuilder<T> withDefinitionMap(Map childrenPropertyMap) {
        if(childrenPropertyMap != null) {
            for(Object key : childrenPropertyMap.keySet()) {
                Object value = childrenPropertyMap.get(key);

                LOGGER.trace("Key [{}], Value [{}]", key, value);
                Validate.isTrue(key instanceof String, "Keys can only be of type String");
                Validate.isTrue(value == null || value instanceof Map, "Values can only be of type Map or null");

                String property = (String) key;
                Map grandChildrenPropertyMap = (Map) value;
                
                DomainGraphDefinitionBuilder<?> childBuilder = getAndSetChildBuilder(property);
                childBuilder.withDefinitionMap(grandChildrenPropertyMap);
            }
        }
        return this;
    }
    
    public DomainGraphDefinition<T> build() {
        Validate.isTrue(clazz!=null && domainResolver != null, "Class and DomainResolver are required for building DomainGraphDefinition");
        DomainDefinition<T> domainDefinition = DomainDefinition.getInstance(clazz, domainResolver);
        DomainGraphDefinition<T> domainGraphDefinition = new DomainGraphDefinitionImpl<T>(domainDefinition);

        for(String childName : childrenBuilders.keySet()) {
            domainGraphDefinition.addChild(childName, childrenBuilders.get(childName).build());
        }
        return domainGraphDefinition;
    }

    /**
     * Returns Map representation of the graph.
     *
     * @return
     */
    public Map getGraph() {
        return build().getGraph();
    }

}
