package org.springframework.data.orient.object.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.orient.object.OrientDbObjectTestConfiguration;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.domain.Address;
import org.springframework.data.orient.object.domain.Person;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { OrientDbObjectTestConfiguration.class })
public class PersonRepositoryTest {

  @Autowired
  OrientObjectOperations operations;

  @Autowired
  PersonRepository repository;

  @Autowired
  OrientObjectDatabaseFactory factory;

  @Before
  public void setUp() {

    //cleanup
    operations.command("delete from Address");
    operations.command("delete from Person");

    List<Address> esenina = operations
        .command("insert into Address (country, city, street) values ('Belarus', 'Minsk', 'Esenina')");

    operations.command("insert into Person (firstName, lastName, active, address) values (?, ?, ?, ?)", "Dzmitry", "Naskou", true,
        esenina.get(0));
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

    Person result = repository.findById(rid).get();

    assertEquals(result.getFirstName(), person.getFirstName());
    assertEquals(result.getLastName(), person.getLastName());
  }

  @Test
  public void findAllPersons() {
    assertThat(repository.findAll()).isNotEmpty().hasSize(5);

  }

  @Test
  public void countPerson() {
    assertThat(repository.count()).isEqualTo(5L);
  }

  @Test
  public void findByFirstNamePage() {

    assertThat(repository.findByFirstName("Jameson")).hasSize(1);
    Page<Person> page = repository.findByFirstName("Jameson", new PageRequest(0, 5));

    for (Person person : repository.findByFirstName("Dzmitry", new PageRequest(0, 5)).getContent()) {
      System.out.println(person);
      assertEquals(person.getFirstName(), "Dzmitry");
    }
  }

  @Test
  public void findAllPaged() {

    assertThat(repository.findAll(new PageRequest(0, 5)).getContent()).hasSize(5);
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

    assertThat(repository.findByFirstNameLike("Dzm%")).hasSize(1).first().hasFieldOrPropertyWithValue("firstName", "Dzmitry");
  }

  @Test
  public void findByLastName() {
    assertThat(repository.findByLastName("Naskou")).hasSize(1);
  }

  @Test
  public void findByLastNameLike() {
    assertThat(repository.findByLastNameLike("Na%")).hasSize(1);
  }

  @Test
  public void findByFirstNameAndLastName() {
    assertThat(repository.findByFirstNameAndLastName("Dzmitry", "Naskou")).hasSize(1);
  }

  @Test
  public void findByFirstNameOrLastName() {
    assertThat(repository.findByFirstNameOrLastName("Dzmitry", "Eliot")).hasSize(2);
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

    assertThat(repository.findByAddress_City("Minsk")).isNotEmpty().hasSize(1).first().extracting("address").extracting("city")
        .hasSize(1).contains("Minsk");
  }

  @Test
  public void findByCountryTest() {

    assertThat(repository.findByAddress_Country("Belarus")).isNotEmpty().hasSize(1).first().extracting("address")
        .extracting("country").hasSize(1).contains("Belarus");
  }

  @Test
  public void deleteByActive() {
    assertThat(repository.deleteByActive(false)).isEqualTo(1);
    assertThat(repository.deleteByActive(true)).isEqualTo(4);
    assertThat(repository.count()).isEqualTo(0);
  }

  @Test
  public void countByActive() {
    assertThat(repository.countByActive(false)).isEqualTo(1);
    assertThat(repository.countByActive(true)).isEqualTo(4);
  }

  @Test
  public void countByFirstNameAndActive() {

    assertThat(repository.countByFirstNameAndActive("Dzmitry", true)).isEqualTo(1);

  }

}

