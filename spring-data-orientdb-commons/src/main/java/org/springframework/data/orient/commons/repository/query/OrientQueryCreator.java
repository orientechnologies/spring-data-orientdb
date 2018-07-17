package org.springframework.data.orient.commons.repository.query;

import org.jooq.*;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.orient.commons.repository.DefaultSource;
import org.springframework.data.orient.commons.repository.OrientSource;
import org.springframework.data.orient.commons.repository.annotation.Cluster;
import org.springframework.data.orient.commons.repository.annotation.Source;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class OrientQueryCreator extends AbstractQueryCreator<String, Condition> {

  private static final Logger logger = LoggerFactory.getLogger(OrientQueryCreator.class);

  private final DSLContext context;

  private final PartTree tree;

  private final OrientParameterAccessor accessor;

  private final OrientQueryMethod method;

  private final ParamType paramType;

  private final Class<?> domainClass;

  public OrientQueryCreator(PartTree tree, OrientQueryMethod method, OrientParameterAccessor parameters) {
    this(tree, method, parameters, ParamType.NAMED);
  }

  public OrientQueryCreator(PartTree tree, OrientQueryMethod method, OrientParameterAccessor parameters, ParamType paramType) {
    super(tree, parameters);

    this.method = method;
    this.context = DSL.using(SQLDialect.MYSQL);
    this.tree = tree;
    this.accessor = parameters;
    this.paramType = paramType;
    this.domainClass = method.getEntityInformation().getJavaType();
  }

  @Override
  protected Condition create(Part part, Iterator<Object> iterator) {
    return toCondition(part, iterator);
  }

  @Override
  protected Condition and(Part part, Condition base, Iterator<Object> iterator) {
    return base.and(toCondition(part, iterator));
  }

  @Override
  protected Condition or(Condition base, Condition criteria) {
    return base.or(criteria);
  }

  public boolean isCountQuery() {
    return tree.isCountProjection();
  }

  public boolean isDeleteQuery() {
    return tree.isDelete();
  }

  @Override
  protected String complete(Condition criteria, Sort sort) {
    Pageable pageable = accessor.getPageable();

    if (tree.isDelete()) {
      return completeDelete(criteria, sort);
    }

    SelectSelectStep<? extends Record> selectStep;

    if (isCountQuery()) {
      selectStep = context.selectCount();
    } else if (tree.isDistinct()) {
      selectStep = context.selectDistinct();
    } else {
      selectStep = context.select();
    }

    SelectConditionStep<? extends Record> conditionStep = selectStep.from(QueryUtils.toSource(getSource())).where(criteria);

    SelectLimitStep<? extends Record> limitStep = orderByIfRequired(conditionStep, pageable, sort);

    Query query = limitIfPageable(limitStep, pageable, sort);

    //TODO: Fix it!!
    //String queryString = query.getSQL(paramType);
    //Use inline parameters for paged queries
    String queryString = query.getSQL(ParamType.INLINED);
    logger.debug(queryString);

    return queryString;
  }

  private String completeDelete(Condition criteria, Sort sort) {
    return context.deleteFrom(table(QueryUtils.toSource(getSource()))).where(criteria).getSQL(ParamType.INDEXED);

  }

  protected Condition toCondition(Part part, Iterator<Object> iterator) {
    String property = part.getProperty().toDotPath();
    Field<Object> field = field(property);

    switch (part.getType()) {
    case AFTER:
    case GREATER_THAN:
      return field.gt(iterator.next());
    case GREATER_THAN_EQUAL:
      return field.ge(iterator.next());
    case BEFORE:
    case LESS_THAN:
      return field.lt(iterator.next());
    case LESS_THAN_EQUAL:
      return field.le(iterator.next());
    case BETWEEN:
      return field.between(iterator.next(), iterator.next());
    case IS_NULL:
      return field.isNull();
    case IS_NOT_NULL:
      return field.isNotNull();
    case IN:
      return field.in(toList(iterator));
    case NOT_IN:
      return field.notIn(toList(iterator));
    case LIKE:
      return lowerIfIgnoreCase(part, field, iterator);
    case NOT_LIKE:
      return lowerIfIgnoreCase(part, field, iterator).not();
    case STARTING_WITH:
      return field.startsWith(iterator.next());
    case ENDING_WITH:
      return field.endsWith(iterator.next());
    case CONTAINING:
      return field.contains(iterator.next());
    case SIMPLE_PROPERTY:
      return field.eq(iterator.next());
    case NEGATING_SIMPLE_PROPERTY:
      return field.ne(iterator.next());
    case TRUE:
      return field.eq(true);
    case FALSE:
      return field.eq(false);
    default:
      throw new IllegalArgumentException("Unsupported keyword!");
    }
  }

  protected OrientSource getSource() {
    OrientSource orientSource = accessor.getSource();

    if (orientSource != null) {
      return orientSource;
    }

    Source source = findAnnotation(Source.class);
    if (source != null) {
      return new DefaultSource(source.type(), source.value());
    }

    Cluster cluster = findAnnotation(Cluster.class);
    if (cluster != null) {
      return new DefaultSource(cluster.value());
    }

    return new DefaultSource(domainClass);
  }

  @SuppressWarnings("incomplete-switch")
  private Condition lowerIfIgnoreCase(Part part, Field<Object> field, Iterator<Object> iterator) {
    switch (part.shouldIgnoreCase()) {
    case ALWAYS:
    case WHEN_POSSIBLE:
      return field.likeIgnoreCase(iterator.next().toString());
    }

    return field.like(iterator.next().toString());
  }

  private List<Object> toList(Iterator<Object> iterator) {
    if (iterator == null || !iterator.hasNext()) {
      return Collections.emptyList();
    }

    List<Object> list = new ArrayList<Object>();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }

    return list;
  }

  private List<SortField<?>> toOrders(Sort sort) {
    List<SortField<?>> orders = new ArrayList<SortField<?>>();

    for (Order order : sort) {
      orders.add(field(order.getProperty()).sort(order.getDirection() == Direction.ASC ? SortOrder.ASC : SortOrder.DESC));
    }

    return orders;
  }

  private SelectLimitStep<? extends Record> orderByIfRequired(SelectConditionStep<? extends Record> conditionStep,
      Pageable pageable, Sort sort) {
    if (isCountQuery()) {
      return conditionStep;
    }

    if (sort == null) {
      return pageable == null ? conditionStep : conditionStep.and(field("@rid").gt(pageable.getOffset()));
    }

//        if (sort == null) {
//            return conditionStep;
//        }
    return conditionStep.orderBy(toOrders(sort));

  }

  private Query limitIfPageable(SelectLimitStep<? extends Record> limitStep, Pageable pageable, Sort sort) {
    if (pageable == null || pageable.isUnpaged() || isCountQuery()) {
      return limitStep;
    } else if (sort == null) {
      return limitStep.limit(pageable.getPageSize());
    }

    return limitStep.limit(pageable.getPageSize()).offset((int) pageable.getOffset());

  }

  private <A extends Annotation> A findAnnotation(Class<A> annotationType) {
    A annotation = AnnotationUtils.findAnnotation(method.getMethod(), annotationType);

    if (annotation == null) {
      annotation = AnnotationUtils.findAnnotation(method.getRepositoryInterface(), annotationType);
    }

    return annotation;
  }
}
