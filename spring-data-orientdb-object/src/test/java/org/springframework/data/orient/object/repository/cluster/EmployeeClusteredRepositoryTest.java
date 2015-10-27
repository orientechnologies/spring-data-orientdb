package org.springframework.data.orient.object.repository.cluster;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.orient.commons.repository.DefaultCluster;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.springframework.data.orient.object.OrientDbObjectTestConfiguration.EMPLOYEE_TMP_CLUSTER;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TransactionConfiguration(defaultRollback = false)
@ContextConfiguration(classes = OrientDbObjectTestConfiguration.class)
@Transactional
public class EmployeeClusteredRepositoryTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeClusteredRepositoryTest.class);

    @Autowired
    OrientObjectDatabaseFactory dbf;

    @Autowired
    EmployeeClusteredRepository repository;

    @Autowired
    EmployeeTmpRepository tmpRepository;

    @Autowired
    OrientObjectOperations operations;

    @BeforeClass
    public void before() {
        operations.command("insert into cluster:employee (firstName, lastName, active) values ('Dzmitry', 'Naskou', true)");
        operations.command("insert into cluster:employee (firstName, lastName, active) values ('Koby', 'Eliot', true)");
        operations.command("insert into cluster:employee_tmp (firstName, lastName, active) values ('Ronny', 'Carlisle', true)");
        operations.command("insert into cluster:employee_tmp (firstName, lastName, active) values ('Jameson', 'Matthew', true)");
        operations.command("insert into cluster:employee_history (firstName, lastName, active) values ('Roydon', 'Brenden', false)");
    }

    @Test
    public void findAll() {
        logger.debug("Employees: {}", repository.findAll());
    }

    @Test
    public void findAll2() {
        for (Employee employee : repository.findAll()) {
            logger.debug(operations.getClusterNameByRid(employee.getRid()));
        }
    }

    @Test
    public void saveEmployeeToDefaultClusterTest() {
        Employee employee = new Employee();
        employee.setFirstName("Ivan");
        employee.setLastName("Ivanou");

        repository.save(employee);
    }

    @Test
    public void saveEmployeeToClusterTest() {
        Employee employee = new Employee();
        employee.setFirstName("Dzmitry");
        employee.setLastName("Naskou");

        repository.save(employee, EMPLOYEE_TMP_CLUSTER);
    }

    @Test
    public void findAllByCluster() {
        logger.debug("Employees: {}", repository.findAll(EMPLOYEE_TMP_CLUSTER));
    }

    @Test
    public void checkClasses() {
        OObjectDatabaseTx db = dbf.openDatabase();

        for (OClass c : db.getMetadata().getSchema().getClasses()) {
            logger.debug("Class: {}", c);
        }

        db.close();
    }

    @Test
    public void checkClusters() {
        OObjectDatabaseTx db = dbf.openDatabase();

        for (String cluster : db.getClusterNames()) {
            logger.debug(cluster);
        }

        db.close();
    }

    @Test
    public void getEmployeeClusters() {
        OObjectDatabaseTx db = dbf.openDatabase();

        for (int i : db.getMetadata().getSchema().getClass(Employee.class).getClusterIds()) {
            logger.debug("Cluster ID: {}", i);
        }

        db.close();
    }

    @Test
    public void findByFirstNameByCluster() {
        List<Employee> results = repository.findByFirstName("Dzmitry", new DefaultCluster(EMPLOYEE_TMP_CLUSTER));
        Assert.assertFalse(results.isEmpty());
    }

    @Test
    public void findByFirstNamePaged() {
        Page<Employee> results = repository.findByFirstNameOrderByFirstName("Dzmitry", new PageRequest(0, 1));
        Assert.assertFalse(results.getContent().isEmpty());
    }

    @Test
    public void findByIds() {
        Page<Employee> employees = repository.findByFirstNameOrderByFirstName("Dzmitry", new PageRequest(0, 1));
        List<String> ids = new ArrayList<>();
        List<ORecordId> oRecordIds = new ArrayList<>();
        for (Employee p : employees.getContent()) {
            ids.add(p.getRid());
            ORecordId oRecordId = new ORecordId();
            oRecordId.fromString(p.getRid());
            oRecordIds.add(oRecordId);
        }
        List<Employee> results1 = repository.queryByRidIn(oRecordIds);
        Assert.assertFalse(results1.isEmpty());

        List<Employee> results2 = repository.findAll(ids);
        Assert.assertFalse(results2.isEmpty());

    }

    @Test
    public void findByLastNameTest() {
        logger.debug("Employee: {}", repository.findByLastName("Naskou"));
    }

    @Test
    public void saveEmployeeToTmpCluster() {
        Employee employee = new Employee();
        employee.setFirstName("Jay");
        employee.setLastName("Miner");

        String rid = tmpRepository.save(employee).getRid();

        Employee result = tmpRepository.findOne(rid);

        assertEquals(result.getFirstName(), employee.getFirstName());
        assertEquals(result.getLastName(), employee.getLastName());
    }

    @Test
    public void repositoryAutowiring() {
        assertNotNull(repository);
    }

    @Test
    public void tmpRepositoryAutowiring() {
        assertNotNull(tmpRepository);
    }

    @Test
    public void countEmployee() {
        assertEquals(repository.count(), 5);
    }

    @Test
    public void countEmployeeTmp() {
        assertEquals(tmpRepository.count(), 2);
    }
}
