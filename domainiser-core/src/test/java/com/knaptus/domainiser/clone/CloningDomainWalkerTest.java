package com.knaptus.domainiser.clone;

import com.knaptus.domainiser.core.DomainDefinition;
import com.knaptus.domainiser.core.impl.DomainGraphDefinitionImpl;
import com.knaptus.domainiser.core.DomainResolver;
import com.knaptus.domainiser.example.Address;
import com.knaptus.domainiser.example.ExampleDomainResolver;
import com.knaptus.domainiser.example.Person;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Unit test
 *
 * @author Aditya Bhardwaj
 */
public class CloningDomainWalkerTest {

    public static final String FRIEND_1 = "Friend1";
    public static final String FRIEND_2 = "Friend2";
    private CloningDomainWalker cloningDomainWalker;

    private Person grandDad;

    @Before
    public void setUp() throws Exception {
        cloningDomainWalker = new CloningDomainWalker();

        DomainResolver domainResolver = new ExampleDomainResolver();
        cloningDomainWalker.setDomainResolver(domainResolver);

        grandDad = new Person("Grand Dad", 80);
        Person grandMom = new Person("Grand Mom", 79);
        grandDad.setSpouse(grandMom);

        Person dad = new Person("Dad", 50);
        Person mom = new Person("Mom", 49);
        dad.setSpouse(mom);

        grandDad.addChild(dad);
        grandMom.addChild(dad);

        Person child1 = new Person("Child 1", 10);
        dad.addChild(child1);
        mom.addChild(child1);

        Person child2 = new Person("Child 2", 15);
        dad.addChild(child2);
        mom.addChild(child2);

        Person grandDadFriend1 = new Person(FRIEND_1, 80);
        Person grandDadFriend2 = new Person(FRIEND_2, 80);
        grandDad.addFriend(grandDadFriend1.getName(), grandDadFriend1);
        grandDad.addFriend(grandDadFriend2.getName(), grandDadFriend2);

        Address address1 = new Address();
        address1.setLine1("address1");
        grandDad.addAddress(address1);

        Address address2 = new Address();
        address2.setLine1("address2");
        grandDad.addAddress(address2);

    }

    @Test
    public void testWalkDefault() throws Exception {
        Person person = cloningDomainWalker.walk(grandDad);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNull(person.getSpouse());
        assertNotNull(person.getChildren());
        assertEquals(0, person.getChildren().size());
    }

    @Test
    public void testWalkDefaultWithKeepReferences() throws Exception {
        cloningDomainWalker.setKeepReferences(true);

        Person person = cloningDomainWalker.walk(grandDad);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNotNull(person.getSpouse());
        assertSame(grandDad.getSpouse(), person.getSpouse());
        assertEquals(1, person.getChildren().size());
        assertSame("Dad object should be the same", grandDad.getChildren().get(0), person.getChildren().get(0));
        assertEquals(2, person.getFriends().size());
        assertSame("Friend1 object should be the same",
                grandDad.getFriends().get(FRIEND_1),
                person.getFriends().get(FRIEND_1));

        assertEquals(2, person.getAddresses().size());

        //one of the cloned address should not match any of the address from the original object.
        boolean exactMatchFound = false;
        Address clonedAddress = person.getAddresses().iterator().next();
        for(Address address : grandDad.getAddresses()) {
            if(address ==  clonedAddress) {
                exactMatchFound = true;
            }
        }
        assertTrue("Atleast one should match by reference", exactMatchFound);
    }

    @Test
    public void testWalkCopySpouse() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinitionImpl<Person> domainGraphDefinition = new DomainGraphDefinitionImpl<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("spouse", personDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNotNull(person.getSpouse());
        assertNotSame(grandDad.getSpouse(), person.getSpouse());
        assertEquals(0, person.getChildren().size());
        assertEquals(0, person.getFriends().size());
    }

    @Test
    public void testWalkCopyChildren() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinitionImpl<Person> domainGraphDefinition = new DomainGraphDefinitionImpl<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("children", personDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNull(person.getSpouse());
        assertNotNull(person.getChildren());
        assertEquals(1, person.getChildren().size());
        assertNotSame("Dad object should not be the same", grandDad.getChildren().get(0), person.getChildren().get(0));
        assertEquals(0, person.getFriends().size());
    }

    @Test
    public void testWalkCopyFriendsAndChildren() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinitionImpl<Person> domainGraphDefinition = new DomainGraphDefinitionImpl<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("children", personDomainDefinition);
        domainGraphDefinition.addChild("friends", personDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNull(person.getSpouse());
        assertEquals("Dad should be copied", 1, person.getChildren().size());
        assertNotSame("Dad object should not be the same", grandDad.getChildren().get(0), person.getChildren().get(0));
        assertEquals("Friends should be copied", 2, person.getFriends().size());
        Assert.assertNotSame("Friend1 object should not be the same",
                grandDad.getFriends().get(FRIEND_1),
                person.getFriends().get(FRIEND_1));
    }

    @Test
    public void testWalkCopyAddresses() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainDefinition<Address> addressDomainDefinition = DomainDefinition.getInstance(Address.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinitionImpl<Person> domainGraphDefinition = new DomainGraphDefinitionImpl<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("addresses", addressDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNull(person.getSpouse());
        assertEquals("Dad shouldn't have been  copied", 0, person.getChildren().size());
        assertEquals("Friends should be empty", 0, person.getFriends().size());
        Assert.assertEquals(2, person.getAddresses().size());

        //one of the cloned address should not match any of the address from the original object.
        Address clonedAddress = person.getAddresses().iterator().next();
        for(Address address : grandDad.getAddresses()) {
            Assert.assertNotSame(address, clonedAddress);
        }
    }

    @Test
    @Ignore
    public void testWalkCopySpouseAndChildrenCrossRefCheck() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinitionImpl<Person> domainGraphDefinition = new DomainGraphDefinitionImpl<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("children", personDomainDefinition);
        domainGraphDefinition.addChild("spouse", personDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNotNull(person.getSpouse());
        assertEquals("Dad should be copied", 1, person.getChildren().size());
        assertNotSame("Dad object should not be the same", grandDad.getChildren().get(0), person.getChildren().get(0));
        assertNotNull(person.getSpouse().getChildren());
        assertEquals("Dad should be copied to grandmom also", 1, person.getSpouse().getChildren().size());
    }


}
