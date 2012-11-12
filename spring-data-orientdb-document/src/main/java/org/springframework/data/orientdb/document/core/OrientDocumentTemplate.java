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

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.orientdb.document.core.convert.OrientDocumentConverter;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentMappingUtils;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentEntity;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentProperty;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class OrientDocumentTemplate implements OrientDocumentOperations {

	private OrientDocumentConverter converter;
	private OrientDocumentDbManager dbManager;
	private final MappingContext<? extends OrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty> mappingContext;
	
	public OrientDocumentTemplate(OrientDocumentDbManager dbManager, OrientDocumentConverter converter) {
		this.converter = converter;
		this.dbManager = dbManager;
		this.mappingContext = this.converter.getMappingContext();
	}
	
	public OrientDocumentTemplate(OrientDocumentDbManager dbFactory) {
		this (dbFactory, null);
	}
	
	public String getCollectionName(Class<?> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> boolean collectionExists(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean collectionExists(String collectionName) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> void dropCollection(Class<T> entityClass) {
		// TODO Auto-generated method stub

	}

	public void dropCollection(String collectionName) {
		// TODO Auto-generated method stub

	}

	public <T> List<T> findAll(Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T findById(Object id, Class<T> entityClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public void insert(Object objectToSave) {
		// TODO Auto-generated method stub

	}

	public void insert(Object objectToSave, String collectionName) {
		// TODO Auto-generated method stub

	}

	public void insertAll(Collection<? extends Object> objectsToSave) {
		// TODO Auto-generated method stub

	}

	public void save(Object objectToSave) {
		save (objectToSave, determineCollectionName(objectToSave.getClass()));
	}

	/* TODO: this is an extremely simple implementation to see if the design works */
	public void save(Object objectToSave, String collectionName) {
		ODocument oDoc = new ODocument(collectionName);
		this.converter.write(objectToSave, oDoc);
		oDoc.save();
	}

	private String determineCollectionName(Class<?> entityClass) {

		if (entityClass == null) {
			throw new InvalidDataAccessApiUsageException(
					"No class parameter provided, entity collection can't be determined!");
		}

		OrientDocumentPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass);
		if (entity == null) {
			throw new InvalidDataAccessApiUsageException("No Persitent Entity information found for the class "
					+ entityClass.getName());
		}
		return entity.getCollection();
	}
	
	public void remove(Object object) {
		// TODO Auto-generated method stub

	}

	public void remove(Object object, String collectionName) {
		// TODO Auto-generated method stub

	}

	public OrientDocumentConverter getConverter() {
		return converter;
	}

	
}
