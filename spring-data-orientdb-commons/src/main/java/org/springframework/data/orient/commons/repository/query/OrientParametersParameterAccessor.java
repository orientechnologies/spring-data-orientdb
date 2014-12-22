package org.springframework.data.orient.commons.repository.query;

import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.repository.query.ParametersParameterAccessor;

public class OrientParametersParameterAccessor extends ParametersParameterAccessor implements OrientParameterAccessor {

    private final OrientParameters parameters;
    
    private final Object[] values;
    
    public OrientParametersParameterAccessor(OrientParameters parameters, Object[] values) {
        super(parameters, values);
        
        this.parameters = parameters;
        this.values = values;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.query.OrientParameterAccessor#getSource()
     */
    @Override
    public OrientSource getSource() {
        if (!parameters.hasSourceParameter()) {
            return null;
        }
        
        return (OrientSource) values[parameters.getSourceIndex()];
    }
}
