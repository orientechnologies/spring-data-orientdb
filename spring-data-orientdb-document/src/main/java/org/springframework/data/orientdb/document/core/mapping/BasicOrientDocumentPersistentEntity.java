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

import java.util.Comparator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class BasicOrientDocumentPersistentEntity<T> extends BasicPersistentEntity<T, OrientDocumentPersistentProperty> implements
OrientDocumentPersistentEntity<T>, ApplicationContextAware {

	private final String collection;
	private final SpelExpressionParser parser;
	private final StandardEvaluationContext context;

	public BasicOrientDocumentPersistentEntity(TypeInformation<T> typeInformation) {

		super(typeInformation, OrientDocumentPersistentPropertyComparator.INSTANCE);

		this.parser = new SpelExpressionParser();
		this.context = new StandardEvaluationContext();

		Class<?> rawType = typeInformation.getType();
		String fallback = OrientDocumentMappingUtils.getPreferredCollectionName(rawType);

		if (rawType.isAnnotationPresent(Document.class)) {
			Document d = rawType.getAnnotation(Document.class);
			this.collection = StringUtils.hasText(d.collection()) ? d.collection() : fallback;
		} else {
			this.collection = fallback;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context.addPropertyAccessor(new BeanFactoryAccessor());
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));
		context.setRootObject(applicationContext);
	}

	/**
	 * Returns the collection the entity should be stored in.
	 * 
	 * @return
	 */
	public String getCollection() {

		Expression expression = parser.parseExpression(collection, ParserContext.TEMPLATE_EXPRESSION);
		return expression.getValue(context, String.class);
	}

	static enum OrientDocumentPersistentPropertyComparator implements Comparator<OrientDocumentPersistentProperty> {

		INSTANCE;

		public int compare(OrientDocumentPersistentProperty o1, OrientDocumentPersistentProperty o2) {

			if (o1.getFieldOrder() == Integer.MAX_VALUE) {
				return 1;
			}

			if (o2.getFieldOrder() == Integer.MAX_VALUE) {
				return -1;
			}

			return o1.getFieldOrder() - o2.getFieldOrder();
		}
	}
}
