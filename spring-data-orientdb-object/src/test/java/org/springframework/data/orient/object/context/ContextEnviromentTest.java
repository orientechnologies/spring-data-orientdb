package org.springframework.data.orient.object.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.SpringProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@EnableTransactionManagement
@ContextConfiguration(classes = OrientDbObjectTestConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ContextEnviromentTest {

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
