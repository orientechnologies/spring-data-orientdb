package org.springframework.data.orient.commons.repository.query;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.springframework.data.orient.commons.core.OrientOperations;

public class StringBasedOrientQuery extends AbstractOrientQuery {
    
    private final String queryString;
    
    private final boolean isCountQuery;
    
    public StringBasedOrientQuery(String query, OrientQueryMethod method, OrientOperations operations) {
        super(method, operations);
        this.queryString = query;
        this.isCountQuery = method.hasAnnotatedQuery() ? method.getQueryAnnotation().count() : false;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected OSQLQuery doCreateQuery(Object[] values) {
        OrientParameterAccessor accessor = new OrientParametersParameterAccessor(getQueryMethod().getParameters(), values);
        String sortedQuery = QueryUtils.applySorting(queryString, accessor.getSort());
        
        return new OSQLSynchQuery(sortedQuery);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected OSQLQuery doCreateCountQuery(Object[] values) {
        return new OSQLSynchQuery<ODocument>(queryString);
    }

    @Override
    protected boolean isCountQuery() {
        return this.isCountQuery;
    }
}
