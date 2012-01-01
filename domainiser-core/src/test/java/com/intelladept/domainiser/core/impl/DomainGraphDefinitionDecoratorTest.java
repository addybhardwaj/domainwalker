package com.intelladept.domainiser.core.impl;

import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.example.Address;
import com.intelladept.domainiser.example.ExampleDomainResolver;
import com.intelladept.domainiser.example.Person;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test
 *
 * @author Aditya Bhardwaj
 */
public class DomainGraphDefinitionDecoratorTest {
    public static final ExampleDomainResolver DOMAIN_RESOLVER = new ExampleDomainResolver();
    private DomainGraphDefinitionDecorator domainGraphDefinitionDecorator;

    private DomainGraphDefinition domainGraphDefinition;

    @Before
    public void before() {
        domainGraphDefinition = Mockito.mock(DomainGraphDefinition.class);
        domainGraphDefinitionDecorator = new DomainGraphDefinitionDecorator(domainGraphDefinition);
    }

    @Test
    public void testGetDomainDefinition() throws Exception {
        DomainDefinition domainDefinition = DomainDefinition.getInstance(Person.class, DOMAIN_RESOLVER);
        when(domainGraphDefinition.getDomainDefinition()).thenReturn(domainDefinition);

        DomainDefinition result = domainGraphDefinitionDecorator.getDomainDefinition();

        assertEquals(domainDefinition, result);
    }

    @Test
    public void testGetDomainClass() throws Exception {
        when(domainGraphDefinition.getDomainClass()).thenReturn(Person.class);

        Class result = domainGraphDefinitionDecorator.getDomainClass();

        assertEquals(Person.class, result);

    }

    @Test
    public void testGetAllChildrenNames() throws Exception {
        Set<String> children = domainGraphDefinitionDecorator.getAllChildrenNames();
        assertEquals(0, children.size());

        HashSet hashSet = new HashSet();
        hashSet.add("something");
        when(domainGraphDefinition.getAllChildrenNames()).thenReturn(hashSet);
        children = domainGraphDefinitionDecorator.getAllChildrenNames();
        assertEquals(1, children.size());
    }


    @Test
    public void testGetChildNullCheck() throws Exception {

        assertNull(domainGraphDefinitionDecorator.getChild("property"));
        assertNull(domainGraphDefinitionDecorator.getChild("property", Address.class));
    }

    @Test
    public void testGetName() throws Exception {
        String definitionName = domainGraphDefinitionDecorator.getName();

        assertEquals("root", definitionName);

        final String property1 = "property1";
        when(domainGraphDefinition.getChild(property1)).thenReturn(new DomainGraphDefinitionImpl(DomainDefinition.getInstance(Person.class, DOMAIN_RESOLVER)));
        DomainGraphDefinition childDef = domainGraphDefinitionDecorator.getChild(property1);

        definitionName = childDef.getName();
        assertEquals("root."+property1 , definitionName);

        when(domainGraphDefinition.getChild(property1, Address.class)).thenReturn(new DomainGraphDefinitionImpl(DomainDefinition.getInstance(Person.class, DOMAIN_RESOLVER)));
        childDef = domainGraphDefinitionDecorator.getChild(property1, Address.class);
        definitionName = childDef.getName();
        assertEquals("root."+property1 , definitionName);
    }


    @Test
    public void testAddChild() throws Exception {
        String something1 = "something1";
        DomainDefinition<Person> domainDefinition = DomainDefinition.getInstance(Person.class, DOMAIN_RESOLVER);
        domainGraphDefinitionDecorator.addChild(something1, domainDefinition);
        verify(domainGraphDefinition, times(1)).addChild(something1, domainDefinition);

        domainGraphDefinitionDecorator.addChild(something1, domainGraphDefinition);
        verify(domainGraphDefinition, times(1)).addChild(something1, domainGraphDefinition);
    }

    @Test
    public void testAddChildDomainGraph() throws Exception {
        domainGraphDefinitionDecorator.addChild("", (DomainGraphDefinition<?>) null);
    }
}
