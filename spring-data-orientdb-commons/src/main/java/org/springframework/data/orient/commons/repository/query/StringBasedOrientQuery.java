package org.springframework.data.orient.commons.repository.query;

import org.springframework.data.orient.commons.core.OrientOperations;

public class StringBasedOrientQuery extends AbstractOrientQuery {

    private final String queryString;

    private final boolean isCountQuery;
    private final boolean isDeleteQuery;

    public StringBasedOrientQuery(String query, OrientQueryMethod method, OrientOperations operations) {
        super(method, operations);
        this.queryString = query;
        this.isCountQuery = method.hasAnnotatedQuery() ? method.getQueryAnnotation().count() : false;
        isDeleteQuery = query.toLowerCase().contains("delete");

    }

    @Override
    protected boolean isCountQuery() {
        return this.isCountQuery;
    }

    @Override
    protected boolean isDeleteQuery() {
        return isDeleteQuery;
    }

    @Override
    protected String toSql(Object[] values) {
        return queryString;
    }

    @Override
    protected String toSqlCount(Object[] values) {
        return queryString;
    }

}
