package org.springframework.data.orient.object.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.data.orient.commons.repository.annotation.Detach;
import org.springframework.data.orient.commons.repository.annotation.FetchPlan;
import org.springframework.data.orient.commons.repository.annotation.Query;
import org.springframework.data.orient.object.domain.Person;

import java.util.List;


public interface PersonRepository extends OrientObjectRepository<Person> {

    //    @Query("select from person where firstName = ?")
    List<Person> findByFirstName(String firstName);

    Page<Person> findByFirstName(String firstName, Pageable pageable);

    List<Person> findByLastName(String lastName);

    List<Person> findByLastNameLike(String lastName);

    List<Person> findByFirstNameAndLastName(String firstName, String lastName);

    List<Person> findByFirstNameOrLastName(String firstName, String lastName);

    List<Person> findByFirstNameLike(String string);

    List<Person> findByFirstNameStartsWith(String firstName);

    Long countByFirstName(String firstName);

    Long countByActive(Boolean active);

    @Query(value = "select count(*) from person where firstName = ? and active = ?", count = true)
    Long countByFirstNameAndActive(String firstName, Boolean active);

    @Detach(DetachMode.ALL)
    List<Person> findByAddress_City(String city);

    @FetchPlan("*:-1")
    List<Person> findByAddress_Country(String country);

    List<Person> findByActiveIsTrue();

    List<Person> findByActiveIsFalse();

    Long deleteByActive(Boolean active);


}
