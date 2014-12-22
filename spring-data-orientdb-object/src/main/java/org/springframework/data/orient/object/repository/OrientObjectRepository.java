package org.springframework.data.orient.object.repository;

import org.springframework.data.orient.commons.repository.OrientRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The specific extension for {@link com.orientechnologies.orient.object.db.OObjectDatabaseTx} database.
 *
 * @author Dzmitry_Naskou
 * @param <T> the generic type to handle
 */
@NoRepositoryBean
public interface OrientObjectRepository<T> extends OrientRepository<T> {
    
    T detachAll(T entity);
}
