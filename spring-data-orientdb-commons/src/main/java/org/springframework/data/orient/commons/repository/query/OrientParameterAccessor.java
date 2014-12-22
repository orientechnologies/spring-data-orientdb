package org.springframework.data.orient.commons.repository.query;

import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.repository.query.ParameterAccessor;

public interface OrientParameterAccessor extends ParameterAccessor {

    OrientSource getSource();
}
