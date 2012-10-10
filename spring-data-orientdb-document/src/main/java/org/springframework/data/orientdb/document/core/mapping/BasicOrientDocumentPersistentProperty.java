/*
 * Copyright (c) 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.orientdb.document.core.mapping;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.util.StringUtils;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class BasicOrientDocumentPersistentProperty extends AnnotationBasedPersistentProperty<OrientDocumentPersistentProperty> implements
OrientDocumentPersistentProperty {

	private static final Logger LOG = LoggerFactory.getLogger(BasicOrientDocumentPersistentProperty.class);
	private static final String ID_FIELD_NAME = "_id";
	private static final Set<Class<?>> SUPPORTED_ID_TYPES = new HashSet<Class<?>>();
	private static final Set<String> SUPPORTED_ID_PROPERTY_NAMES = new HashSet<String>();

	static {
//		SUPPORTED_ID_TYPES.add(ObjectId.class);
		SUPPORTED_ID_TYPES.add(String.class);
		SUPPORTED_ID_TYPES.add(BigInteger.class);

		SUPPORTED_ID_PROPERTY_NAMES.add("id");
//		SUPPORTED_ID_PROPERTY_NAMES.add("_id");
	}

	public BasicOrientDocumentPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
			OrientDocumentPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
		super(field, propertyDescriptor, owner, simpleTypeHolder);

		if (isIdProperty() && getFieldName() != ID_FIELD_NAME) {
			LOG.warn("Customizing field name for id property not allowed! Custom name will not be considered!");
		}
	}

//	@Override
//	public boolean isAssociation() {
//		return field.isAnnotationPresent(DBRef.class) || super.isAssociation();
//	}

	@Override
	public boolean isIdProperty() {

		if (super.isIdProperty()) {
			return true;
		}

		return SUPPORTED_ID_PROPERTY_NAMES.contains(field.getName());
	}

	/**
	 * Returns the key to be used to store the value of the property inside a Mongo {@link DBObject}.
	 * 
	 * @return
	 */
	public String getFieldName() {

		if (isIdProperty()) {
			return ID_FIELD_NAME;
		}

		org.springframework.data.orientdb.document.core.mapping.Field annotation = getField().getAnnotation(
				org.springframework.data.orientdb.document.core.mapping.Field.class);
		return annotation != null && StringUtils.hasText(annotation.value()) ? annotation.value() : field.getName();
	}

	public int getFieldOrder() {
		org.springframework.data.orientdb.document.core.mapping.Field annotation = getField().getAnnotation(
				org.springframework.data.orientdb.document.core.mapping.Field.class);
		return annotation != null ? annotation.order() : Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.mapping.AbstractPersistentProperty#createAssociation()
	 */
	@Override
	protected Association<OrientDocumentPersistentProperty> createAssociation() {
		return new Association<OrientDocumentPersistentProperty>(this, null);
	}

//	public boolean isDbReference() {
//		return getField().isAnnotationPresent(DBRef.class);
//	}
//
//	public DBRef getDBRef() {
//		return getField().getAnnotation(DBRef.class);
//	}
}
