package org.springframework.data.orient.object.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.domain.Address;
import org.springframework.data.orient.object.domain.Person;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrientDbObjectTestConfiguration.class})
public class PersonRepositoryTest {

    @Autowired
    OrientObjectOperations operations;

    @Autowired
    PersonRepository repository;

    @Autowired
    OrientObjectDatabaseFactory factory;

    @Before
    public void setUp() {

        operations.command("delete from Address");
        operations.command("delete from Person");

        Address esenina = operations.command("insert into Address (country, city, street) values ('Belarus', 'Minsk', 'Esenina')");

        operations.command("insert into Person (firstName, lastName, active, address) values (?, ?, ?, ?)", "Dzmitry", "Naskou", true, esenina);
        operations.command("insert into Person (firstName, lastName, active) values ('Koby', 'Eliot', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Ronny', 'Carlisle', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Jameson', 'Matthew', true)");
        operations.command("insert into Person (firstName, lastName, active) values ('Roydon', 'Brenden', false)");
    }


    @Test
    public void repositoryAutowiring() {
        Assert.assertNotNull(repository);
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
        assertThat(repository.findByActiveIsTrue()).hasSize(4);
    }

    @Test
    public void findByActiveIsFalse() {
        assertThat(repository.findByActiveIsFalse()).hasSize(1);
    }

    @Test
    public void findByCityTest() {
        List<Person> persons = repository.findByAddress_City("Minsk");

        assertFalse(persons.isEmpty());

        for (Person person : persons) {
            assertEquals(person.getAddress().getCity(), "Minsk");
        }
    }


    @Test
    public void deleteByActive() {

        assertThat(repository.deleteByActive(false)).isEqualTo(1);
        assertThat(repository.deleteByActive(true)).isEqualTo(4);


    }


}
