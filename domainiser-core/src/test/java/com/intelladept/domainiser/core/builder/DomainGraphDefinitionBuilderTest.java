package com.intelladept.domainiser.core.builder;

import com.intelladept.domainiser.core.DomainGraphDefinition;
import com.intelladept.domainiser.example.ExampleDomainResolver;
import com.intelladept.domainiser.example.Person;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

/**
 * Unit test
 *
 * @author Aditya Bhardwaj
 */
public class DomainGraphDefinitionBuilderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainGraphDefinitionBuilderTest.class);

    public static final String SPOUSE = "spouse";
    public static final String CHILDREN = "children";
    public static final String ADDRESSES = "addresses";

    @Test
    public void testBuildSampleGraphDef() throws Exception {
        DomainGraphDefinitionBuilder<Person> builder = new DomainGraphDefinitionBuilder<Person>();
        builder.forClass(Person.class)
                .withResolver(new ExampleDomainResolver())
                .withSinglePath(SPOUSE, CHILDREN, ADDRESSES);
        DomainGraphDefinition<Person> sample = builder.build();
        LOGGER.info("JSON representation \n [{}]", sample.toString());
        assertNotNull(sample.getChild(SPOUSE));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN).getChild(ADDRESSES));
    }

    @Test
    public void testBuildSampleGraphDefMultiLevel() throws Exception {
        DomainGraphDefinitionBuilder<Person> builder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        builder.withPathsDotNotation("spouse.addresses")
                .withSinglePath(SPOUSE, CHILDREN, ADDRESSES)
        ;
        DomainGraphDefinition<Person> sample = builder.build();
        LOGGER.info("JSON representation \n [{}]", sample.toString());
        assertNotNull(sample.getChild(SPOUSE));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN));
        assertNotNull(sample.getChild(SPOUSE).getChild(ADDRESSES));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN).getChild(ADDRESSES));
    }

    @Test
    public void testBuildSampleGraphDefWithBuilder() throws Exception {
        DomainGraphDefinitionBuilder<Person> builder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        DomainGraphDefinitionBuilder<Person> childBuilder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        childBuilder.withSinglePath(CHILDREN, ADDRESSES);

        builder.withDomainGraphDefinitionBuilder(childBuilder, SPOUSE);

        DomainGraphDefinition<Person> sample = builder.build();
        LOGGER.info("JSON representation \n [{}]", sample.toString());
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN).getChild(ADDRESSES));
    }

    @Test
    public void testBuildSampleGraphDefWithBuilderMultiLevel() throws Exception {
        DomainGraphDefinitionBuilder<Person> builder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        builder.withPathsDotNotation("spouse.addresses");
        DomainGraphDefinitionBuilder<Person> childBuilder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        childBuilder.withSinglePath(CHILDREN, ADDRESSES);

        builder.withDomainGraphDefinitionBuilder(childBuilder, SPOUSE);

        DomainGraphDefinition<Person> sample = builder.build();
        LOGGER.info("JSON representation \n [{}]", sample.toString());
        assertNotNull(sample.getChild(SPOUSE).getChild(ADDRESSES));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN).getChild(ADDRESSES));
    }

    @Test
    public void testBuildWithJson() {
        DomainGraphDefinitionBuilder<Person> builder = new DomainGraphDefinitionBuilder<Person>(new ExampleDomainResolver(), Person.class);
        builder.withDefinitionJson("{spouse:{children:{addresses:null},addresses:{}}}");

        DomainGraphDefinition<Person> sample = builder.build();
        LOGGER.info("JSON representation \n [{}]", sample.toString());
        assertNotNull(sample.getChild(SPOUSE).getChild(ADDRESSES));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN));
        assertNotNull(sample.getChild(SPOUSE).getChild(CHILDREN).getChild(ADDRESSES));
    }
}
