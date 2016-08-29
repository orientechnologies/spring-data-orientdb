package org.springframework.boot.orientdb.hello.repository;

import org.springframework.boot.orientdb.hello.data.Person;
import org.springframework.data.orient.commons.repository.annotation.Query;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import java.util.List;

public interface PersonRepository extends OrientObjectRepository<Person> {
    
    List<Person> findByFirstName(String firstName);
    
    @Query("select from person where lastName = ?")
    List<Person> findByLastName(String lastName);
    
    List<Person> findByAge(Integer age);
    
    @Query("delete from Person where age = ?")
    Long deleteByAge(Integer age);
}
