package org.springframework.boot.orientdb.hello.repository;

import org.springframework.boot.orientdb.hello.data.Person;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.data.orient.commons.repository.annotation.Detach;
import org.springframework.data.orient.commons.repository.annotation.Query;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import java.util.List;

public interface PersonRepository extends OrientObjectRepository<Person> {

    @Override
    @Detach(DetachMode.ALL)
    List<Person> findAll();

    List<Person> findByFirstName(String firstName);

    @Query("select from person where lastName = ?")
    List<Person> findByLastName(String lastName);

    List<Person> findByAge(Integer age);

    Long deleteByAge(Integer age);
}
