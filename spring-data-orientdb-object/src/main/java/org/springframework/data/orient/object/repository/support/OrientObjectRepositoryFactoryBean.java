package org.springframework.data.orient.object.repository.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

import java.io.Serializable;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @author Dzmitry_Naskou
 * 
 * @param <T> the type of the repository
 * @param <S> the type of the entity to handle
 * @param <ID> the type of the entity identifier to handle
 */
public class OrientObjectRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

    /** The orient operations. */
    @Autowired
    private OrientObjectOperations operations;

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        return new OrientObjectRepositoryFactory(operations);
    }
}
