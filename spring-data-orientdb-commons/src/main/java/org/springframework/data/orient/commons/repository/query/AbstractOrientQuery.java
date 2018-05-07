package org.springframework.data.orient.commons.repository.query;

import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.data.orient.commons.repository.query.OrientQueryExecution.*;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * The base class to implement {@link org.springframework.data.repository.query.RepositoryQuery}s for OrientDB.
 */
public abstract class AbstractOrientQuery implements RepositoryQuery {

    /**
     * The query method.
     */
    private final OrientQueryMethod method;

    /**
     * The object operations.
     */
    private final OrientOperations operations;

    /**
     * Instantiates a new {@link AbstractOrientQuery}.
     *
     * @param method     the query method
     * @param operations the orient operations
     */
    public AbstractOrientQuery(OrientQueryMethod method, OrientOperations operations) {
        super();
        this.method = method;
        this.operations = operations;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.RepositoryQuery#getQueryMethod()
     */
    public OrientQueryMethod getQueryMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.query.RepositoryQuery#execute(java.lang.Object[])
     */
    @Override
    public Object execute(Object[] parameters) {
        return doExecute(getExecution(), parameters);
    }

    /**
     * Do execute.
     *
     * @param execution the execution
     * @param values    the values
     * @return the object
     */
    protected Object doExecute(OrientQueryExecution execution, Object[] values) {
        return execution.execute(this, getDetachMode(), values);
    }


    protected abstract String toSql(Object[] params);

    protected abstract String toSqlCount(Object[] params);

    /**
     * Gets the execution for query.
     *
     * @return the execution
     */
    protected OrientQueryExecution getExecution() {
        final OrientParameters parameters = method.getParameters();

        if (method.isCollectionQuery()) {
            return new CollectionExecution(operations, parameters);
        } else if (isCountQuery()) {
            return new CountExecution(operations, parameters);
        } else if (method.isPageQuery()) {
            return new PagedExecution(operations, parameters);
        } else if (method.isQueryForEntity()) {
            return new SingleEntityExecution(operations, parameters);
        } else if (isDeleteQuery()) {
            return new DeleteExecution(operations, parameters);
        }

        throw new IllegalArgumentException();
    }

    /**
     * Checks if is count query.
     *
     * @return true, if is count query
     */
    protected abstract boolean isCountQuery();

    protected abstract boolean isDeleteQuery();

    @SuppressWarnings("rawtypes")
    private OSQLQuery<?> applyFetchPlan(OSQLQuery query) {
        String fetchPlan = method.getFetchPlan();

        if (fetchPlan != null) {
            query.setFetchPlan(fetchPlan);
        }

        return query;
    }

    protected DetachMode getDetachMode() {
        return method.getDetachMode();
    }
}
