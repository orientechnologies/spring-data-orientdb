package org.springframework.data.orient.object.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.domain.Address;
import org.springframework.data.orient.object.domain.Person;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Rollback
@TestExecutionListeners(
        inheritListeners = false,
        listeners = {DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(classes = OrientDbObjectTestConfiguration.class)
@Transactional
public class PersonRepositoryTest extends AbstractTestNGSpringContextTests {
    private static final Logger logger = LoggerFactory.getLogger(PersonRepositoryTest.class);

    @Autowired
    PersonRepository repository;
    
    @Autowired
    OrientObjectDatabaseFactory factory;
    
    @Autowired
    OrientObjectOperations operations;
    
    @BeforeClass
    public void before() {
        Address esenina = operations.command("insert into Address (country, city, street) values ('Belarus', 'Minsk', 'Esenina')");
        
        operations.command("insert into Person (firstName, lastName, active, address) values (?, ?, ?, ?)", "Dzmitry", "Naskou", true, esenina);
        operations.command("insert into Person (firstName, lastName, active) values ('Koby', 'Eliot', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Ronny', 'Carlisle', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Jameson', 'Matthew', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Roydon', 'Brenden', false)");
    }
    
    @Test
    public void repositoryAutowiring() {
        assertNotNull(repository);
    }
    
    @Test
    public void savePerson() {
        Person person = new Person();
        person.setFirstName("Jay");
        person.setLastName("Miner");
        
        String rid = repository.save(person).getRid();
        
        Person result = repository.findOne(rid);
        
        assertEquals(result.getFirstName(), person.getFirstName());
        assertEquals(result.getLastName(), person.getLastName());
    }

    @Test
    public void findAllPersons() {
        assertFalse(repository.findAll().isEmpty());
    }

    @Test
    public void countPerson() {
        assertEquals(repository.count(), 5L);
    }

    @Test
    public void findByFirstNamePage() {
        for (Person person : repository.findByFirstName("Dzmitry", new PageRequest(1, 5)).getContent()) {
            assertEquals(person.getFirstName(), "Dzmitry");
        }
    }

    @Test
    public void countByFirstName() {
        assertEquals(repository.countByFirstName("Dzmitry"), Long.valueOf(1));
    }

    @Test
    public void printFindByLastName() {
        List<Person> result = repository.findByLastName("Naskou");

        assertFalse(result.isEmpty());

        for (Person person : result) {
            assertEquals(person.getLastName(), "Naskou");
        }
    }

    @Test
    public void findByFirstName() {
        assertFalse(repository.findByFirstName("Dzmitry").isEmpty());
    }

    @Test
    public void findByFirstNameLike() {
        for (Person person : repository.findByFirstNameLike("Dzm%")) {
            assertTrue(person.getFirstName().startsWith("Dzm"));
        }
    }

    @Test
    public void findByLastName() {
        assertFalse(repository.findByLastName("Naskou").isEmpty());
    }

    @Test
    public void findByLastNameLike() {
        for (Person person : repository.findByLastNameLike("Na%")) {
            assertTrue(person.getLastName().startsWith("Na"));
        }
    }

    @Test
    public void findByFirstNameAndLastName() {
        for (Person person : repository.findByFirstNameOrLastName("Dzmitry", "Naskou")) {
            assertTrue(person.getFirstName().equals("Dzmitry") && person.getLastName().equals("Naskou"));
        }
    }

    @Test
    public void findByFirstNameOrLastName() {
        for (Person person : repository.findByFirstNameOrLastName("Dzmitry", "Eliot")) {
            assertTrue(person.getFirstName().equals("Dzmitry") || person.getLastName().equals("Eliot"));
        }
    }

    @Test
    public void findByActiveIsTrue() {
        for (Person person : repository.findByActiveIsTrue()) {
            assertTrue(person.getActive());
        }
    }

    @Test
    public void findByActiveIsFalse() {
        for (Person person : repository.findByActiveIsFalse()) {
            assertFalse(person.getActive());
        }
    }

    @Test
    public void findByCityTest() {
        List<Person> persons = repository.findByAddress_City("Minsk");

        assertFalse(persons.isEmpty());

        for (Person person : persons) {
            assertEquals(person.getAddress().getCity(), "Minsk");
        }
    }
}
