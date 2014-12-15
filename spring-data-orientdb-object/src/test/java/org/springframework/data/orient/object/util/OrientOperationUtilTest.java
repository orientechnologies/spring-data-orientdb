package org.springframework.data.orient.object.util;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.domain.Address;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Configuration
@EnableTransactionManagement
@TransactionConfiguration
@ContextConfiguration(classes = OrientDbObjectTestConfiguration.class)
public class OrientOperationUtilTest extends AbstractTestNGSpringContextTests {

    @Autowired
    OrientObjectOperations template;

    @Autowired
    OrientObjectDatabaseFactory factory;
    
    @BeforeClass
    public void before() {
        try (OObjectDatabaseTx db = factory.openDatabase()) {
            db.getEntityManager().registerEntityClass(Employee.class);
        }
    }

    @Test
    public void getRidTest(){
        Address address = new Address();
        Assert.assertNull(template.getRid(address));

        address.setId("123");
        Assert.assertEquals(template.getRid(address), "123");
    }

    @Test
    public void getRidFromParentTest(){
        Employee employee = new Employee();
        Assert.assertNull(template.getRid(employee));

        employee.setRid("123");
        Assert.assertEquals(template.getRid(employee), "123");
    }

    @Test
    public void getRidFromProxy(){
        Employee employee = new Employee();
        Employee savedEmployee = template.save(employee);
        
        Assert.assertNotSame(savedEmployee.getClass(), Employee.class);
        Assert.assertEquals(template.getRid(savedEmployee), savedEmployee.getRid());
    }
}
