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

package org.springframework.data.orientdb.document.core.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.BeanWrapper;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mapping.model.SpELContext;
import org.springframework.data.orientdb.document.core.OrientDocumentDbFactory;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentEntity;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentProperty;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class MappingOrientDocumentConverter extends AbstractOrientDocumentConverter {

	protected static final Logger log = LoggerFactory.getLogger(MappingOrientDocumentConverter.class);

	protected final MappingContext<? extends OrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty> mappingContext;
	protected final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
	protected final OrientDocumentDbFactory dbFactory;
//	protected final QueryMapper idMapper;
	protected ApplicationContext applicationContext;
	protected boolean useFieldAccessOnly = true;
//	protected MongoTypeMapper typeMapper;
//	protected String mapKeyDotReplacement = null;
	
	private SpELContext spELContext;
	
	//TODO: Create the conversion service the right way
	
	public MappingOrientDocumentConverter(OrientDocumentDbFactory dbFactory,
			MappingContext<? extends OrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty> mappingContext) {
		
		super(ConversionServiceFactory.createDefaultConversionService());

		Assert.notNull(dbFactory);
		Assert.notNull(mappingContext);

		this.dbFactory = dbFactory;
		this.mappingContext = mappingContext;
//		this.typeMapper = new DefaultMongoTypeMapper(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY, mappingContext);
//		this.idMapper = new QueryMapper(this);

//		this.spELContext = new SpELContext(DBObjectPropertyAccessor.INSTANCE);
	}
	
	
	public MappingContext<? extends OrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty> getMappingContext() {
		return this.mappingContext;
	}

	public <S extends Object> S read(Class<S> clazz, ODocument oDoc) {
		return null;
	}

	public void write(Object obj, ODocument oDoc) {
		if (null == obj) {
			return;
		}

//		boolean handledByCustomConverter = conversions.getCustomWriteTarget(obj.getClass(), DBObject.class) != null;
		TypeInformation<? extends Object> type = ClassTypeInformation.from(obj.getClass());

//		if (!handledByCustomConverter && !(dbo instanceof BasicDBList)) {
//			typeMapper.writeType(type, dbo);
//		}

		writeInternal(obj, oDoc, type);
	}

	protected void writeInternal(final Object obj, final ODocument oDoc, final TypeInformation<?> typeHint) {

		if (null == obj) {
			return;
		}

//		Class<?> customTarget = conversions.getCustomWriteTarget(obj.getClass(), DBObject.class);
//
//		if (customTarget != null) {
//			DBObject result = conversionService.convert(obj, DBObject.class);
//			dbo.putAll(result);
//			return;
//		}
//
//		if (Map.class.isAssignableFrom(obj.getClass())) {
//			writeMapInternal((Map<Object, Object>) obj, dbo, ClassTypeInformation.MAP);
//			return;
//		}
//
//		if (Collection.class.isAssignableFrom(obj.getClass())) {
//			writeCollectionInternal((Collection<?>) obj, ClassTypeInformation.LIST, (BasicDBList) dbo);
//			return;
//		}

		OrientDocumentPersistentEntity<?> entity = mappingContext.getPersistentEntity(obj.getClass());
		writeInternal(obj, oDoc, entity);
//		addCustomTypeKeyIfNecessary(typeHint, obj, oDoc);
	}
	
	
	protected void writeInternal(Object obj, final ODocument oDoc, OrientDocumentPersistentEntity<?> entity) {

		if (obj == null) {
			return;
		}

		if (null == entity) {
			throw new MappingException("No mapping metadata found for entity of type " + obj.getClass().getName());
		}

		final BeanWrapper<OrientDocumentPersistentEntity<Object>, Object> wrapper = BeanWrapper.create(obj, conversionService);

		// Write the ID
		final OrientDocumentPersistentProperty idProperty = entity.getIdProperty();
//		if (!dbo.containsField("_id") && null != idProperty) {
//
//			try {
//				Object id = wrapper.getProperty(idProperty, Object.class, useFieldAccessOnly);
//				dbo.put("_id", idMapper.convertId(id));
//			} catch (ConversionException ignored) {
//			}
//		}

		// Write the properties
		entity.doWithProperties(new PropertyHandler<OrientDocumentPersistentProperty>() {
			public void doWithPersistentProperty(OrientDocumentPersistentProperty prop) {

				if (prop.equals(idProperty)) {
					return;
				}

				Object propertyObj = wrapper.getProperty(prop, prop.getType(), useFieldAccessOnly);

				if (null != propertyObj) {
//					if (!conversions.isSimpleType(propertyObj.getClass())) {
//						writePropertyInternal(propertyObj, dbo, prop);
//					} else {
						writeSimpleInternal(propertyObj, oDoc, prop.getFieldName());
//					}
				}
			}
		});

	}
	
	private void writeSimpleInternal(Object value, ODocument oDoc, String key) {
		oDoc.field(key, getPotentiallyConvertedSimpleWrite(value));
	}
	
	private Object getPotentiallyConvertedSimpleWrite(Object value) {

		if (value == null) {
			return null;
		}

		return value;
		
//		Class<?> customTarget = conversions.getCustomWriteTarget(value.getClass(), null);
//
//		if (customTarget != null) {
//			return conversionService.convert(value, customTarget);
//		} else {
//			return Enum.class.isAssignableFrom(value.getClass()) ? ((Enum<?>) value).name() : value;
//		}
	}
	
}
