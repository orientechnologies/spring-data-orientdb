package org.springframework.data.orient.commons.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * The Orient specific extension of {@link org.springframework.data.repository.Repository}.
 *
 * @author Dzmitry_Naskou
 * @param <T> the generic type to handle
 */
@NoRepositoryBean
public interface OrientRepository<T> extends PagingAndSortingRepository<T, String> {
    
    /**
     * Saves a given entity to the given cluster. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param entity the entity
     * @param cluster the cluster name
     * @return the saved entity
     */
    <S extends T> S save(S entity, String cluster);
    
    /**
     * Returns the number of entities available with the given cluster.
     * 
     * @return the number of entities
     */
    long count(String cluster);

    /**
     * Returns the number of entities available with the given type.
     * 
     * @return the number of entities
     */
    long count(Class<? extends T> domainClass);
    
    /**
     * Returns the number of entities available with the given {@link OrientSource}.
     *
     * @param source the source
     * @return the long
     */
    long count(OrientSource source);
    
    /**
     * Gets the domain class for repository.
     *
     * @return the domain class
     */
    Class<T> getDomainClass();
    
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    @Override
    List<T> findAll();
    
    /**
     * Returns all instances of the type with the given cluster.
     *
     * @param cluster the cluster name
     * @return the list
     */
    List<T> findAll(String cluster);
    
    /**
     * Returns all instances of the type with the given source.
     *
     * @param source the source
     * @return the list
     */
    List<T> findAll(OrientSource source);
    
    /**
     * Returns all instances with the given type.
     *
     * @param domainClass the domain class
     * @return the list
     */
    <S extends T> List<S> findAll(Class<S> domainClass);
    
    /* (non-Javadoc)
     * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Sort)
     */
    @Override
    List<T> findAll(Sort sort);
    
    /* (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
     */
    List<T> findAll(Iterable<String> ids);
    
    /**
     * Deletes all entities managed by the repository for the given cluster.
     *
     * @param cluster the cluster name
     */
    void deleteAll(String cluster);
    
    /**
     * Deletes all entities with the given type managed by the repository.
     *
     * @param domainClass the domain class
     */
    void deleteAll(Class<? extends T> domainClass);


}
