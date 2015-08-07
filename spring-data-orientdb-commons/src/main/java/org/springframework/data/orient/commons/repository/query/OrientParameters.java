package org.springframework.data.orient.commons.repository.query;

import org.springframework.core.MethodParameter;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.repository.query.Parameters;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class OrientParameters extends Parameters<OrientParameters, OrientParameter> {

    private final int sourceIndex;
    private final List<OrientParameter> originals; // parent's parameters attribute is not accessible

    private OrientParameters(List<OrientParameter> originals) {
        super(originals);

        this.originals = originals;

        int clusterIndexTemp = -1;

        for (int i = 0; i < originals.size(); i++) {
            OrientParameter original = originals.get(i);
            clusterIndexTemp = original.isSource() ? i : -1;
        }

        sourceIndex = clusterIndexTemp;
    }

    public OrientParameters(Method method) {
        super(method);

        originals = new ArrayList<>();

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

    @Override
    public void forEach(Consumer<? super OrientParameter> action) {
        originals.forEach(action);
    }

    @Override
    public Spliterator<OrientParameter> spliterator() {
        return originals.spliterator();
    }
}
