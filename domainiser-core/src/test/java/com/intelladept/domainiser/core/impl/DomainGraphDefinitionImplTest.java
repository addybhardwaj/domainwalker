package com.intelladept.domainiser.core.impl;

import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.core.DomainResolver;
import com.intelladept.domainiser.example.Address;
import com.intelladept.domainiser.example.ExampleDomainResolver;
import com.intelladept.domainiser.example.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.*;

/**
 * Unit Test
 *
 * @author Aditya Bhardwaj
 */
public class DomainGraphDefinitionImplTest {

    private DomainGraphDefinitionImpl<Person> personDomainGraphDefinition;

    private DomainResolver domainResolver;

    @Before
    public void setUp() throws Exception {
        domainResolver = new ExampleDomainResolver();
        personDomainGraphDefinition = new DomainGraphDefinitionImpl<Person>(DomainDefinition.getInstance(Person.class, domainResolver));
    }

    @Test
    public void testGetDomainClass() throws Exception {
        assertEquals(Person.class, personDomainGraphDefinition.getDomainClass());
        assertEquals("Person", personDomainGraphDefinition.getName());
    }

    @Test
    public void testAddAndGetChild() throws Exception {
        String homeAddress = "home";
        DomainGraphDefinitionImpl<Address> domainGraphDefinition = new DomainGraphDefinitionImpl<Address>(DomainDefinition.getInstance(Address.class, domainResolver));
        personDomainGraphDefinition.addChild(homeAddress, domainGraphDefinition);
        Set<String> children = personDomainGraphDefinition.getAllChildrenNames();
        assertNotNull(children.contains(homeAddress));

    }

    @Test
    public void testAddAndGetListChild() throws Exception {
        String children = "children";
        personDomainGraphDefinition.addChild(children, new DomainGraphDefinitionImpl<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
        Set<String> childrenDef = personDomainGraphDefinition.getAllChildrenNames();
        assertNotNull(childrenDef.contains(children));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidChild() throws Exception {
        String address = "address";
        personDomainGraphDefinition.addChild(address, new DomainGraphDefinitionImpl<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
    }

    @Test
    public void testFindDomainGraphDefinition() throws Exception {
        DomainGraphDefinition<Address> homeDomainGraphDef =
                personDomainGraphDefinition.getChild("home", Address.class);
        assertNull(homeDomainGraphDef);

        personDomainGraphDefinition.addChild("home", new DomainGraphDefinitionImpl<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
        homeDomainGraphDef =
                personDomainGraphDefinition.getChild("home", Address.class);
        assertNotNull(homeDomainGraphDef);

        DomainGraphDefinition<?> domainGraphDef =
                personDomainGraphDefinition.getChild("home");
        assertNotNull(domainGraphDef);

        DomainGraphDefinition<Person> someDomainGraphDef =
                personDomainGraphDefinition.getChild("home", Person.class);
        assertNull(someDomainGraphDef);

    }


}
