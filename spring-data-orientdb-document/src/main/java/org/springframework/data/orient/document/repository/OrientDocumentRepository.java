package org.springframework.data.orient.document.repository;

import org.springframework.data.orient.commons.repository.OrientRepository;

/**
 * The specific extension for document database.
 *
 * @author Dzmitry_Naskou
 * @param <T> the generic type to handle
 */
public interface OrientDocumentRepository<T> extends OrientRepository<T> {

}
