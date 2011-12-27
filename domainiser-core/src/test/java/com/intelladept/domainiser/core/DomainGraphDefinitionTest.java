package com.intelladept.domainiser.core;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Unit Test
 *
 * @author Aditya Bhardwaj
 */
public class DomainGraphDefinitionTest {

    private DomainGraphDefinition<Person> personDomainGraphDefinition;

    private DomainResolver domainResolver;

    @Before
    public void setUp() throws Exception {
        domainResolver = new DomainResolver() {
            @Override
            public boolean isDomainModel(Class domain) {
                return domain.getCanonicalName().contains("intelladept");
            }
        };
        personDomainGraphDefinition = new DomainGraphDefinition<Person>(DomainDefinition.getInstance(Person.class, domainResolver));
    }

    @Test
    public void testGetDomainClass() throws Exception {
        assertEquals(Person.class, personDomainGraphDefinition.getDomainClass());

    }

    @Test
    public void testAddAndGetChild() throws Exception {
        String homeAddress = "home";
        DomainGraphDefinition<Address> domainGraphDefinition = new DomainGraphDefinition<Address>(DomainDefinition.getInstance(Address.class, domainResolver));
        personDomainGraphDefinition.addChild(homeAddress, domainGraphDefinition);
        Map<String, DomainGraphDefinition<?>> children = personDomainGraphDefinition.getAllChildren();
        assertNotNull(children.get(homeAddress));

    }

    @Test
    public void testAddAndGetListChild() throws Exception {
        String children = "children";
        personDomainGraphDefinition.addChild(children, new DomainGraphDefinition<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
        Map<String, DomainGraphDefinition<?>> childrenDef = personDomainGraphDefinition.getAllChildren();
        assertNotNull(childrenDef.get(children));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddInvalidChild() throws Exception {
        String address = "address";
        personDomainGraphDefinition.addChild(address, new DomainGraphDefinition<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
    }

    @Test
    public void testFindDomainGraphDefinition() throws Exception {
        DomainGraphDefinition<Address> homeDomainGraphDef =
                personDomainGraphDefinition.getChild("home", Address.class);
        assertNull(homeDomainGraphDef);

        personDomainGraphDefinition.addChild("home", new DomainGraphDefinition<Address>(DomainDefinition.getInstance(Address.class, domainResolver)));
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

    /**
     * Following domain objects are created for testing only *
     */

    public static class Person {
        private String name;
        private int age;
        private Person spouse;
        private List<Person> children = new ArrayList<Person>();
        private Set<Address> addresses   = new HashSet<Address>();
        private Map<String, Person> friends = new HashMap<String, Person>();
        private Address home;
        private Address office;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public List<Person> getChildren() {
            return children;
        }

        public void setChildren(List<Person> children) {
            this.children = children;
        }

        public void addChild(Person child) {
            this.children.add(child);
        }

        public Set<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(Set<Address> addresses) {
            this.addresses = addresses;
        }

        public void addAddress(Address address) {
            this.addresses.add(address);
        }

        public Map<String, Person> getFriends() {
            return friends;
        }

        public void setFriends(Map<String, Person> friends) {
            this.friends = friends;
        }

        public void addFriend(String name, Person friend) {
            this.friends.put(name, friend);
        }

        public Address getHome() {
            return home;
        }

        public void setHome(Address home) {
            this.home = home;
        }

        public Address getOffice() {
            return office;
        }

        public void setOffice(Address office) {
            this.office = office;
        }

        public Person getSpouse() {
            return spouse;
        }

        public void setSpouse(Person spouse) {
            this.spouse = spouse;
            if (spouse != null && spouse.getSpouse() == null) {
                spouse.setSpouse(this);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public static class Address {
        private String line1;
        private String line2;
        private String line3;

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getLine3() {
            return line3;
        }

        public void setLine3(String line3) {
            this.line3 = line3;
        }
    }


}
