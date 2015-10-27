package org.springframework.data.orient.commons.repository.support;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.orient.commons.repository.OrientRepository;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.orient.commons.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of the {@link org.springframework.data.repository.PagingAndSortingRepository} interface for OrientDB.
 * 
 * @author Dzmitry_Naskou
 * @param <T> the type of the entity to handle
 */
@Repository
@Transactional(readOnly = true)
public class SimpleOrientRepository<T> implements OrientRepository<T> {

    /** The orient operations. */
    protected final OrientOperations<T> operations;
    
    /** The domain class. */
    protected final Class<T> domainClass;
    
    protected final String source;
    
    /** The repository interface. */
    protected final Class<?> repositoryInterface;
    
    private final OrientStrategy<T> strategy;

    /**
     * Instantiates a new {@link SimpleOrientRepository} from the given {@link OrientOperations} and domain class.
     *
     * @param operations the orinet operations
     * @param domainClass the domain class
     * @param repositoryInterface the target repository interface
     */
    public SimpleOrientRepository(OrientOperations operations, Class<T> domainClass, Class<?> repositoryInterface) {
        this(operations, domainClass, repositoryInterface, new SimpleOrientStrategy<T>(operations, domainClass));
    }

    /**
     * Instantiates a new simple orient repository.
     *
     * @param operations the operations
     * @param domainClass the domain class
     * @param cluster the cluster
     * @param repositoryInterface the repository interface
     */
    public SimpleOrientRepository(OrientOperations operations, Class<T> domainClass, String cluster, Class<?> repositoryInterface) {
        this(operations, domainClass, repositoryInterface, new ClusteredOrientStrategy<T>(operations, cluster));
    }

    /**
     * Instantiates a new simple orient repository.
     *
     * @param operations the operations
     * @param domainClass the domain class
     * @param repositoryInterface the repository interface
     * @param strategy the strategy
     */
    private SimpleOrientRepository(OrientOperations operations, Class<T> domainClass, Class<?> repositoryInterface, OrientStrategy<T> strategy) {
        this.operations = operations;
        this.domainClass = domainClass;
        this.repositoryInterface = repositoryInterface;
        this.strategy = strategy;
        this.source = strategy.getSource();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(S)
     */
    @Transactional(readOnly = false)
    public <S extends T> S save(S entity) {
        return strategy.save(entity);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#save(java.lang.Object, java.lang.String)
     */
    @Override
    @Transactional(readOnly = false)
    public <S extends T> S save(S entity, String cluster) {
        return operations.save(entity, cluster);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    @Transactional(readOnly = false)
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        List<S> result = new ArrayList<S>();

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Transactional(readOnly = false)
    public <S extends T> Iterable<S> save(Iterable<S> entities, String cluster) {
        if (entities == null) {
            return Collections.emptyList();
        }

        List<S> result = new ArrayList<S>();

        for (S entity : entities) {
            result.add(save(entity, cluster));
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
     */
    public T findOne(String id) {
        return operations.load(new ORecordId(id));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    public boolean exists(String id) {
        return findOne(id) != null;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#findAll()
     */
    @Override
    public List<T> findAll() {
        return operations.query(getQuery((Sort) null));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#findAll(java.lang.Class)
     */
    @Override
    public <S extends T> List<S> findAll(Class<S> domainClass) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#findAll(java.lang.String)
     */
    @Override
    public List<T> findAll(String cluster) {
        return operations.query(getQuery(QueryUtils.clusterToSource(cluster), (Sort) null));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#findAll(org.springframework.data.orient.repository.Source)
     */
    @Override
    public List<T> findAll(OrientSource source) {
        return operations.query(getQuery(QueryUtils.toSource(source), (Sort) null));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#findAll(java.lang.Iterable)
     */
    @Override
    public List<T> findAll(Iterable<String> ids) {
        List<ORecordId> oRecordIds = new ArrayList<>();
        for (String id : ids) {
            ORecordId oRecordId = new ORecordId();
            oRecordId.fromString(id);
            oRecordIds.add(oRecordId);
        }
        return operations.query(getQuery("?", null), oRecordIds);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        return strategy.count();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#count(java.lang.String)
     */
    @Override
    public long count(String cluster) {
        return operations.countClusterElements(cluster);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#count(java.lang.Class)
     */
    @Override
    public long count(Class<? extends T> domainClass) {
        return operations.countClass(domainClass);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#count(org.springframework.data.orient.repository.OrientSource)
     */
    @Override
    public long count(OrientSource source) {
        switch (source.getSourceType()) {
            case CLUSTER: return count(source.getName());
            case CLASS: return operations.countClass(source.getName());
            default: throw new IllegalArgumentException();
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Transactional(readOnly = false)
    public void delete(String id) {
        operations.delete(new ORecordId(id));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Transactional(readOnly = false)
    public void delete(T entity) {
        operations.delete(entity);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Transactional(readOnly = false)
    public void delete(Iterable<? extends T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#deleteAll()
     */
    @Transactional(readOnly = false)
    public void deleteAll() {
        for (T entity : findAll()) {
            delete(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#deleteAll(java.lang.String)
     */
    @Override
    public void deleteAll(String cluster) {
        for (T entity : findAll(cluster)) {
            delete(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#deleteAll(java.lang.Class)
     */
    @Override
    public void deleteAll(Class<? extends T> domainClass) {
        for (T entity : findAll(domainClass)) {
            delete(entity);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.OrientRepository#getDomainClass()
     */
    @Override
    public Class<T> getDomainClass() {
        return domainClass;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    public List<T> findAll(Sort sort) {
        return operations.query(getQuery(sort));
    }

    public List<T> findAll(String cluster, Sort sort) {
        return operations.query(getQuery(QueryUtils.clusterToSource(cluster), sort));
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Page<T> findAll(Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<T>(findAll());
        }

        Long total = count();
        List<T> content = (List<T>) (total > pageable.getOffset() ? operations.query(getQuery(pageable)) : Collections.<T> emptyList());

        return new PageImpl<T>(content, pageable, total);
    }

    /**
     * Creates the query for the given {@link org.springframework.data.domain.Sort}.
     *
     * @param sort the sort
     * @return the query
     */
    private OSQLQuery<T> getQuery(Sort sort) {
        return getQuery(source, sort);
    }

    /**
     * Creates the query for the given {@link OrientSource} and {@link org.springframework.data.domain.Sort}.
     *
     * @param source the source
     * @param sort the sort
     * @return the query
     */
    private OSQLQuery<T> getQuery(String source, Sort sort) {
        Query query = DSL.using(SQLDialect.MYSQL).select().from(source).orderBy(QueryUtils.toOrders(sort));

        return new OSQLSynchQuery<T>(query.getSQL(ParamType.INLINED));
    }

    /**
     * Creates the query for the given {@link org.springframework.data.domain.Sort}.
     *
     * @param pageable the pageable
     * @return the query
     */
    private OSQLQuery<T> getQuery(Pageable pageable) {
        DSLContext context = DSL.using(SQLDialect.MYSQL);
        SelectJoinStep<? extends Record> joinStep = context.select().from(domainClass.getSimpleName());
        
        Sort sort = pageable.getSort();
        SelectLimitStep<? extends Record> limitStep = sort == null ? joinStep : joinStep.orderBy(QueryUtils.toOrders(sort));
        Query query = pageable == null ? limitStep : limitStep.limit(pageable.getPageSize()).offset(pageable.getOffset());
        
        return new OSQLSynchQuery<T>(query.getSQL(ParamType.INLINED));
    }
}
