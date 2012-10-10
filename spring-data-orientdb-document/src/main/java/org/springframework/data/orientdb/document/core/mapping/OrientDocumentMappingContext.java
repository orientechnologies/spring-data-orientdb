/*
 * Copyright (c) 2012 by the original author(s).
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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class OrientDocumentMappingContext extends AbstractMappingContext<BasicOrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty>
		implements ApplicationContextAware {

	private ApplicationContext context;

	public OrientDocumentMappingContext() {
//		setSimpleTypeHolder(MongoSimpleTypes.HOLDER);
	}

//	@Override
//	protected boolean shouldCreatePersistentEntityFor(TypeInformation<?> type) {
//		return !MongoSimpleTypes.HOLDER.isSimpleType(type.getType());
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.mapping.AbstractMappingContext#createPersistentProperty(java.lang.reflect.Field, java.beans.PropertyDescriptor, org.springframework.data.mapping.MutablePersistentEntity, org.springframework.data.mapping.SimpleTypeHolder)
	 */
	@Override
	public OrientDocumentPersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor,
			BasicOrientDocumentPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
//		return new CachingMongoPersistentProperty(field, descriptor, owner, simpleTypeHolder);
		return new BasicOrientDocumentPersistentProperty(field, descriptor, owner, simpleTypeHolder);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.mapping.BasicMappingContext#createPersistentEntity(org.springframework.data.util.TypeInformation, org.springframework.data.mapping.model.MappingContext)
	 */
	@Override
	protected <T> BasicOrientDocumentPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {

		BasicOrientDocumentPersistentEntity<T> entity = new BasicOrientDocumentPersistentEntity<T>(typeInformation);

		if (context != null) {
			entity.setApplicationContext(context);
		}

		return entity;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
