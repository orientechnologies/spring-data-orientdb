/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.orientdb.document.core;

import java.util.Collection;
import java.util.List;

import org.springframework.data.orientdb.document.core.convert.OrientDocumentConverter;

/**
 * 
 * @author "Forat Latif"
 *
 */

public interface OrientDocumentOperations {
	
	/* TODO: Should "cluster" be used instead of collection?
	 * Cluster is the orientDB equivalent to MongoDB's Collection
	 */
	
	String getCollectionName(Class<?> entityClass);
	
	<T> boolean collectionExists(Class<T> entityClass);
	
	boolean collectionExists(String collectionName);
	
	<T> void dropCollection(Class<T> entityClass);
	
	void dropCollection(String collectionName);
	
	<T> List<T> findAll(Class<T> entityClass);
	
	<T> T findById(Object id, Class<T> entityClass);
	
	void insert(Object objectToSave);
	
	void insert(Object objectToSave, String collectionName);
	
	void insertAll(Collection<? extends Object> objectsToSave);
	
	/**
	 * Save the object to the collection for the entity type of the object to save. This will perform an insert if the
	 * object is not already present, that is an 'upsert'.
	 */
	
	void save(Object objectToSave);
	
	void save(Object objectToSave, String collectionName);
	
	void remove(Object object);

	void remove(Object object, String collection);

	OrientDocumentConverter getConverter();
	
}