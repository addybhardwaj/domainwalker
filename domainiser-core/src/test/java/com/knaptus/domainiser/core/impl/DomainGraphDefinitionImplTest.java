package com.knaptus.domainiser.core.impl;

import com.knaptus.domainiser.core.DomainDefinition;
import com.knaptus.domainiser.core.DomainGraphDefinition;
import com.knaptus.domainiser.core.DomainResolver;
import com.knaptus.domainiser.example.Address;
import com.knaptus.domainiser.example.ExampleDomainResolver;
import com.knaptus.domainiser.example.Person;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static junit.framework.Assert.*;

/**
 * Unit Test
 *
 * @author Aditya Bhardwaj
 */
public class DomainGraphDefinitionImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainGraphDefinitionImplTest.class);

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
        LOGGER.info(personDomainGraphDefinition.toString());
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
