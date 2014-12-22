package org.springframework.data.orient.commons.repository.query;

import org.springframework.core.MethodParameter;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.repository.query.Parameters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class OrientParameters extends Parameters<OrientParameters, OrientParameter> {

    private final int sourceIndex;
    
    private OrientParameters(List<OrientParameter> originals) {
        super(originals);
        
        int clusterIndexTemp = -1;

        for (int i = 0; i < originals.size(); i++) {
            OrientParameter original = originals.get(i);
            clusterIndexTemp = original.isSource() ? i : -1;
        }
        
        sourceIndex = clusterIndexTemp;
    }

    public OrientParameters(Method method) {
        super(method);
        
        List<Class<?>> types = Arrays.asList(method.getParameterTypes());
        
        sourceIndex = types.indexOf(OrientSource.class);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.Parameters#createParameter(org.springframework.core.MethodParameter)
     */
    @Override
    protected OrientParameter createParameter(MethodParameter parameter) {
        return new OrientParameter(parameter);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.Parameters#createFrom(java.util.List)
     */
    @Override
    protected OrientParameters createFrom(List<OrientParameter> parameters) {
        return new OrientParameters(parameters);
    }
    
    public int getSourceIndex() {
        return sourceIndex;
    }
    
    public boolean hasSourceParameter() {
        return sourceIndex != -1;
    }
}
