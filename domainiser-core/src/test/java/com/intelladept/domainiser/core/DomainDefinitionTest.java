package com.intelladept.domainiser.core;

import com.intelladept.domainiser.example.ExampleDomainResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Unit test
 *
 */
public class DomainDefinitionTest {

    private DomainDefinition domainDefinition;

    private DomainResolver domainResolver;

    @Before
    public void before() {
        domainResolver = new ExampleDomainResolver();
        domainDefinition = DomainDefinition.getInstance(Bean.class, domainResolver


       );
    }
    
    @Test
    public void testInit() throws Exception {

        domainDefinition.init(domainResolver);
        assertEquals(Bean.class, domainDefinition.getUnderlyingDomainModel("children"));
        assertEquals(Bean.class, domainDefinition.getUnderlyingDomainModel("wildCard"));
        assertEquals(Bean.class, domainDefinition.getUnderlyingDomainModel("superWildCard"));
        assertEquals(Object.class, domainDefinition.getUnderlyingDomainModel("completeWildCard"));
        assertEquals(Bean.class, domainDefinition.getUnderlyingDomainModel("lookup"));
        assertNull(domainDefinition.getUnderlyingDomainModel("reverseLookup"));
        assertNull(null, domainDefinition.getUnderlyingDomainModel("ages"));
    }
    
    private static class Bean {
        private int count;
        private Boolean isTrue;
        private Bean child;
        private Bean readOnlyChild;
        private Bean writeOnlyChild;
        private List<Bean> children;
        private List<Integer> ages;
        private Map<String, Bean> lookup;
        private Map<Bean, String> reverseLookup;
        private List<? extends Bean> wildCard;
        private List<?> completeWildCard;
        private List<? super Bean> superWildCard;

        public List<?> getCompleteWildCard() {
            return completeWildCard;
        }

        public void setCompleteWildCard(List<?> completeWildCard) {
            this.completeWildCard = completeWildCard;
        }

        public List<? extends Bean> getWildCard() {
            return wildCard;
        }

        public void setWildCard(List<? extends Bean> wildCard) {
            this.wildCard = wildCard;
        }

        public List<? super Bean> getSuperWildCard() {
            return superWildCard;
        }

        public void setSuperWildCard(List<? super Bean> superWildCard) {
            this.superWildCard = superWildCard;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Boolean getTrue() {
            return isTrue;
        }

        public void setTrue(Boolean aTrue) {
            isTrue = aTrue;
        }

        public Bean getChild() {
            return child;
        }

        public void setChild(Bean child) {
            this.child = child;
        }

        public Bean getReadOnlyChild() {
            return readOnlyChild;
        }

        public void setWriteOnlyChild(Bean writeOnlyChild) {
            this.writeOnlyChild = writeOnlyChild;
        }

        public List<Bean> getChildren() {
            return children;
        }

        public void setChildren(List<Bean> children) {
            this.children = children;
        }

        public List<Integer> getAges() {
            return ages;
        }

        public void setAges(List<Integer> ages) {
            this.ages = ages;
        }

        public Map<String, Bean> getLookup() {
            return lookup;
        }

        public void setLookup(Map<String, Bean> lookup) {
            this.lookup = lookup;
        }

        public Map<Bean, String> getReverseLookup() {
            return reverseLookup;
        }

        public void setReverseLookup(Map<Bean, String> reverseLookup) {
            this.reverseLookup = reverseLookup;
        }
    }
}
