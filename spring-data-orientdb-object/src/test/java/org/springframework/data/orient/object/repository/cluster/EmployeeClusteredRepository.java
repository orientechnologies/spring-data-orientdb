package org.springframework.data.orient.object.repository.cluster;

import com.orientechnologies.orient.core.id.ORecordId;
import org.springframework.data.orient.commons.repository.OrientCluster;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.data.orient.object.repository.OrientObjectRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.orient.commons.repository.annotation.Query;

public interface EmployeeClusteredRepository extends OrientObjectRepository<Employee> {

    List<Employee> findByLastName(String lastName);
    
    List<Employee> findByFirstName(String firstName, OrientCluster cluster);

    Page<Employee> findByFirstNameOrderByFirstName(String dzmitry, Pageable pageRequest);

    List<Employee> findByRidIn(List<ORecordId> oRecordIds);

    @Query("select from ?")
    List<Employee> queryByRidIn(List<ORecordId> oRecordIds);
}
