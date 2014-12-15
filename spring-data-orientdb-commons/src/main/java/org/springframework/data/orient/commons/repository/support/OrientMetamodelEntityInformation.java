package org.springframework.data.orient.commons.repository.support;

import org.springframework.data.repository.core.support.AbstractEntityInformation;

public class OrientMetamodelEntityInformation<T> extends AbstractEntityInformation<T, String> {

        public OrientMetamodelEntityInformation(Class<T> domainClass) {
                super(domainClass);
        }

        public String getId(T entity) {
                throw new UnsupportedOperationException("Not implemented yet.");
        }

        public Class<String> getIdType() {
                return String.class;
        }
}
