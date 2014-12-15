package org.springframework.data.orient.commons.repository.query;

import org.jooq.SortField;
import org.jooq.SortOrder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jooq.impl.DSL.field;

/**
 * Utility class to create Orient queries.
 * 
 * @author Dzmitry_Naskou
 */
public final class QueryUtils {

    private QueryUtils() {
        super();
    }
    
    /**
     * Apply sorting for the given query.
     *
     * @param query the query
     * @param sort the sort
     * @return the string
     */
    public static String applySorting(String query, Sort sort) {
        Assert.hasText(query);
        
        if (null == sort || !sort.iterator().hasNext()) {
            return query;
        }
        
        throw new UnsupportedOperationException("Not implemented");
    }
    
    /**
     * Converts the given {@link org.springframework.data.domain.Sort} into {@link SortField}s.
     *
     * @param sort the {@link org.springframework.data.domain.Sort} instance to be transformed into JOOQ {@link SortField}s.
     * @return the list of {@link SortField}s.
     */
    public static List<SortField<?>> toOrders(Sort sort) {
        if (sort == null) {
            return Collections.emptyList();
        }
        
        List<SortField<?>> orders = new ArrayList<SortField<?>>();
        
        for (Order order : sort) {
            orders.add(field(order.getProperty()).sort(order.getDirection() == Direction.ASC ? SortOrder.ASC : SortOrder.DESC)); 
        }

        return orders;
    }
    
    public static String clusterToSource(String clusterName) {
        return new StringBuilder("cluster:").append(clusterName).toString();
    }
    
    public static String toSource(Class<?> domainClass) {
        return domainClass.getSimpleName();
    }
    
    public static String toSource(OrientSource source) {
        if (source != null) {        
            switch (source.getSourceType()) {
                case CLUSTER: return clusterToSource(source.getName());
                case CLASS: return source.getName();
            }
        }
        
        return null;
    }
}
