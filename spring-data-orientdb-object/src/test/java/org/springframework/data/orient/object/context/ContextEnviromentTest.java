package org.springframework.data.orient.object.context;

import org.springframework.aop.SpringProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Configuration
@EnableTransactionManagement
@ContextConfiguration(classes = OrientDbObjectTestConfiguration.class)
public class ContextEnviromentTest extends AbstractTestNGSpringContextTests{

    @Autowired
    ApplicationContext context;

    @Autowired
    OrientObjectDatabaseFactory dbf;

    @Autowired
    OrientObjectOperations operations;

    @Test
    public void checkApplicationContext() {
        assertNotNull(context);
    }

    @Test
    public void checkOrientObjectDatabaseFactory() {
        assertNotNull(dbf);
    }

    @Test
    public void checkOrientOperations() {
        assertNotNull(operations);
    }

    @Test
    public void checkTransactionalOrientObjectTemplate() {
        assertTrue(SpringProxy.class.isAssignableFrom(operations.getClass()));
    }
}
