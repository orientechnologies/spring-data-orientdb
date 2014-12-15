package org.springframework.data.orient.object.repository.cluster;

import org.springframework.data.orient.commons.repository.OrientCluster;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.data.orient.object.domain.Person;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import java.util.List;

public interface EmployeeClusteredRepository extends OrientObjectRepository<Employee> {

    List<Person> findByLastName(String lastName);
    
    List<Person> findByFirstName(String firstName, OrientCluster cluster);
}
