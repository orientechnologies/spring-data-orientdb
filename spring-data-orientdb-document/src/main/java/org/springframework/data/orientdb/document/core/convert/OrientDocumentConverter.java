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

import org.springframework.data.convert.EntityConverter;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentEntity;
import org.springframework.data.orientdb.document.core.mapping.OrientDocumentPersistentProperty;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * 
 * @author "Forat Latif"
 *
 */

public interface OrientDocumentConverter extends 
	EntityConverter<OrientDocumentPersistentEntity<?>, OrientDocumentPersistentProperty, Object, ODocument>{

}
