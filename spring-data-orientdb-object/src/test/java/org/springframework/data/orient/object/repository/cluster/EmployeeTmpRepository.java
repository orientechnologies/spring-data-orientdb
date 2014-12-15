package org.springframework.data.orient.object.repository.cluster;

import org.springframework.data.orient.commons.repository.annotation.Source;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import static org.springframework.data.orient.object.OrientDbObjectTestConfiguration.EMPLOYEE_TMP_CLUSTER;

@Source(EMPLOYEE_TMP_CLUSTER)
public interface EmployeeTmpRepository extends OrientObjectRepository<Employee> {

}
