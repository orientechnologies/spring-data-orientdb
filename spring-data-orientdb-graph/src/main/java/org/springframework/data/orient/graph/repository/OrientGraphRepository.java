package org.springframework.data.orient.graph.repository;

import org.springframework.data.orient.commons.repository.OrientRepository;

/**
 * The specific extension for graph database.
 *
 * @author Dzmitry_Naskou
 * @param <T> the generic type to handle
 */
public interface OrientGraphRepository<T> extends OrientRepository<T> {

}
