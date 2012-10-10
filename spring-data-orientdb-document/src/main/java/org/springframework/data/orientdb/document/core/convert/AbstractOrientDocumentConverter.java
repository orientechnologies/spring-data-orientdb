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

package org.springframework.data.orientdb.document.core.convert;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConversionServiceFactory;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.convert.EntityInstantiators;

public abstract class AbstractOrientDocumentConverter implements OrientDocumentConverter, InitializingBean {


		protected final GenericConversionService conversionService;
//		protected CustomConversions conversions = new CustomConversions();
		protected EntityInstantiators instantiators = new EntityInstantiators();

		@SuppressWarnings("deprecation")
		public AbstractOrientDocumentConverter(GenericConversionService conversionService) {
			this.conversionService = conversionService == null ? ConversionServiceFactory.createDefaultConversionService()
					: conversionService;
		}

//		public void setCustomConversions(CustomConversions conversions) {
//			this.conversions = conversions;
//		}

		/**
		 * Registers {@link EntityInstantiators} to customize entity instantiation.
		 * 
		 * @param instantiators
		 */
		public void setInstantiators(EntityInstantiators instantiators) {
			this.instantiators = instantiators == null ? new EntityInstantiators() : instantiators;
		}

		/**
		 * Registers additional converters that will be available when using the {@link ConversionService} directly 
		 */
		private void initializeConverters() {

//			if (!conversionService.canConvert(ObjectId.class, String.class)) {
//				conversionService.addConverter(ObjectIdToStringConverter.INSTANCE);
//			}
//			if (!conversionService.canConvert(String.class, ObjectId.class)) {
//				conversionService.addConverter(StringToObjectIdConverter.INSTANCE);
//			}
//			if (!conversionService.canConvert(ObjectId.class, BigInteger.class)) {
//				conversionService.addConverter(ObjectIdToBigIntegerConverter.INSTANCE);
//			}
//			if (!conversionService.canConvert(BigInteger.class, ObjectId.class)) {
//				conversionService.addConverter(BigIntegerToObjectIdConverter.INSTANCE);
//			}
//
//			conversions.registerConvertersIn(conversionService);
		}

		public ConversionService getConversionService() {
			return conversionService;
		}

		/* (non-Javadoc)
		 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
		 */
		public void afterPropertiesSet() {
			initializeConverters();
			instantiators = (instantiators == null) ? new EntityInstantiators() : instantiators;
		}
	
}
