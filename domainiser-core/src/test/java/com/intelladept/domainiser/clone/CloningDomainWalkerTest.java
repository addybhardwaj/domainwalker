package com.intelladept.domainiser.clone;

import static com.intelladept.domainiser.core.DomainGraphDefinitionTest.*;
import static org.junit.Assert.assertNotSame;

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
            public boolean isDomainModel(Object domain) {
                return domain.getClass().getCanonicalName().contains("com.intelladept");
            }
        };
        cloningDomainWalker.setDomainResolver(domainResolver);

        grandDad = createPerson("Grand Dad", 80);
        Person grandMom = createPerson("Grand Mom", 79);
        grandDad.setSpouse(grandMom);

        Person dad = createPerson("Dad", 50);
        Person mom = createPerson("Mom", 49);
        dad.setSpouse(mom);

        grandDad.addChild(dad);
        grandMom.addChild(dad);

        Person child1 = createPerson("Child 1", 10);
        dad.addChild(child1);
        mom.addChild(child1);

        Person child2 = createPerson("Child 2", 15);
        dad.addChild(child2);
        mom.addChild(child2);
    }

    @Test
    public void testWalk() throws Exception {
        Person person = cloningDomainWalker.walk(grandDad);
        assertNotSame(grandDad, person);
    }

    private Person createPerson(String name, int age) {
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        return person;
    }

}
