package com.intelladept.domainiser.clone;

import static com.intelladept.domainiser.core.DomainGraphDefinitionTest.*;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import com.intelladept.domainiser.core.DomainDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.core.DomainGraphDefinitionTest;
import com.intelladept.domainiser.core.DomainResolver;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test
 *
 * @author Aditya Bhardwaj
 */
public class CloningDomainWalkerTest {

    private CloningDomainWalker cloningDomainWalker;

    private Person grandDad;

    @Before
    public void setUp() throws Exception {
        cloningDomainWalker = new CloningDomainWalker();

        DomainResolver domainResolver = new DomainResolver() {
            @Override
            public boolean isDomainModel(Class domain) {
                return domain.getCanonicalName().contains("com.intelladept");
            }
        };
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
    public void testWalkCopyChildren() throws Exception {
        DomainDefinition<Person> personDomainDefinition = DomainDefinition.getInstance(Person.class, cloningDomainWalker.getDomainResolver());
        DomainGraphDefinition<Person> domainGraphDefinition = new DomainGraphDefinition<Person>(personDomainDefinition);
        domainGraphDefinition.addChild("children", personDomainDefinition);

        Person person = cloningDomainWalker.walk(grandDad, domainGraphDefinition);
        assertNotSame(grandDad, person);
        assertEquals(grandDad.getName(), person.getName());
        assertEquals(grandDad.getAge(), person.getAge());
        assertNull(person.getSpouse());
        assertNotNull(person.getChildren());
    }


}
