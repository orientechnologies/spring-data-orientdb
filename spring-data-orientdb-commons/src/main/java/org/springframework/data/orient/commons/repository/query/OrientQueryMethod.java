package org.springframework.data.orient.commons.repository.query;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.data.orient.commons.repository.annotation.Detach;
import org.springframework.data.orient.commons.repository.annotation.FetchPlan;
import org.springframework.data.orient.commons.repository.annotation.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Orient specific extension of {@link org.springframework.data.repository.query.QueryMethod}.
 *
 * @author Dzmitry_Naskou
 */
public final class OrientQueryMethod extends QueryMethod {

    /**
     * The method.
     */
    private final Method method;

    /**
     * The repository interface.
     */
    private final Class<?> repositoryInterface;

    /**
     * Instantiates a new {@link OrientQueryMethod}.
     *
     * @param method   the method
     * @param metadata the metadata
     */
    public OrientQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
        this.repositoryInterface = metadata.getRepositoryInterface();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.QueryMethod#createParameters(java.lang.reflect.Method)
     */
    @Override
    protected OrientParameters createParameters(Method method) {
        return new OrientParameters(method);
    }

    @Override
    public OrientParameters getParameters() {
        return (OrientParameters) super.getParameters();
    }

    /**
     * Gets the target method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Gets the repository interface.
     *
     * @return the repository interface
     */
    public Class<?> getRepositoryInterface() {
        return repositoryInterface;
    }

    /**
     * Returns whether the method has an annotated query.
     *
     * @return
     */
    public boolean hasAnnotatedQuery() {
        return getAnnotatedQuery() != null;
    }

    /**
     * Returns the query string declared in a {@link Query} annotation or {@literal null} if neither the annotation found
     * nor the attribute was specified.
     *
     * @return the query
     */
    String getAnnotatedQuery() {
        String query = (String) AnnotationUtils.getValue(getQueryAnnotation());
        return StringUtils.hasText(query) ? query : null;
    }

    /**
     * Returns the {@link Query} annotation that is applied to the method or {@code null} if none available.
     *
     * @return
     */
    Query getQueryAnnotation() {
        return method.getAnnotation(Query.class);
    }

    FetchPlan getFetchPlanAnnotation() {
        return method.getAnnotation(FetchPlan.class);
    }

    String getFetchPlan() {
        String plan = (String) AnnotationUtils.getValue(getFetchPlanAnnotation());

        return StringUtils.hasText(plan) ? plan : null;
    }

    Detach getDetachAnnotation() {
        return method.getAnnotation(Detach.class);
    }

    DetachMode getDetachMode() {
        DetachMode mode = (DetachMode) AnnotationUtils.getValue(getDetachAnnotation());

        return mode == null ? DetachMode.NONE : mode;
    }
}
