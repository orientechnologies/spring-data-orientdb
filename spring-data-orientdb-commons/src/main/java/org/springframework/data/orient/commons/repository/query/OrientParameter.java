package org.springframework.data.orient.commons.repository.query;

import org.springframework.core.MethodParameter;
import org.springframework.data.orient.commons.repository.OrientCluster;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.repository.query.Parameter;

import java.util.Arrays;
import java.util.List;

public class OrientParameter extends Parameter {
 
    static final List<Class<?>> ORIENT_TYPES = Arrays.asList(OrientSource.class, OrientCluster.class);
    
    static final List<Class<?>> ORIENT_SOURCE_TYPES = Arrays.asList(OrientSource.class, OrientCluster.class);
    
    protected OrientParameter(MethodParameter parameter) {
        super(parameter);
    }

    @Override
    public boolean isSpecialParameter() {
        return super.isSpecialParameter() || ORIENT_TYPES.contains(getType());
    }
    
    @Deprecated
    boolean isCluster() {
        return OrientCluster.class.isAssignableFrom(getType());
    }
    
    /**
     * Checks if the parameter is the source.
     *
     * @return true, if it's source
     */
    boolean isSource() {
        return OrientSource.class.isAssignableFrom(getType());
    }
}
