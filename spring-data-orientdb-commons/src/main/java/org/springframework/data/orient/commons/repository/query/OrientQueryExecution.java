package org.springframework.data.orient.commons.repository.query;

import com.orientechnologies.orient.core.sql.executor.OResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.orient.commons.repository.DetachMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set of classes to contain query execution strategies.
 *
 * @author Dzmitry_Naskou
 */
public abstract class OrientQueryExecution {

  /**
   * The orient object template.
   */
  protected final OrientOperations operations;

  /**
   * The parameters.
   */
  protected final OrientParameters parameters;

  public OrientQueryExecution(OrientOperations template, OrientParameters parameters) {
    super();
    this.operations = template;
    this.parameters = parameters;
  }

  /**
   * Executes the given {@link AbstractOrientQuery} with the given {@link Object[]} values.
   *
   * @param query  the orient query
   * @param values the parameters values
   *
   * @return the result
   */
  public Object execute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
    return doExecute(query, mode, values);
  }

  /**
   * Method to implement by executions.
   *
   * @param query  the orient query
   * @param values the parameters values
   *
   * @return the result
   */
  protected abstract Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values);

  protected Object[] prepareParameters(OrientParameters parameters, Object[] values) {
    int index = 0;
    List<Object> params = new ArrayList<>();

    for (OrientParameter parameter : parameters) {
      if (parameter.isBindable()) {
        params.add(values[index]);
      }

      ++index;
    }

    return params.toArray();
  }

  /**
   * Executes the query to return a simple collection of entities.
   *
   * @author Dzmitry_Naskou
   */
  static class CollectionExecution extends OrientQueryExecution {

    /**
     * Instantiates a new {@link CollectionExecution}.
     *
     * @param template the template
     */
    public CollectionExecution(OrientOperations template, OrientParameters parameters) {
      super(template, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
      return operations.query(query.toSql(values), mode, prepareParameters(parameters, values));
    }
  }

  /**
   * Executes a {@link AbstractOrientQuery} to return a single entity.
   *
   * @author Dzmitry_Naskou
   */
  static class SingleEntityExecution extends OrientQueryExecution {

    /**
     * Instantiates a new {@link SingleEntityExecution}.
     *
     * @param template the template
     */
    public SingleEntityExecution(OrientOperations template, OrientParameters parameters) {
      super(template, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    protected Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
      return operations.queryForObject(query.toSql(values), mode, prepareParameters(parameters, values));
    }
  }

  /**
   * Executes a {@link AbstractOrientQuery} to return a count of entities.
   *
   * @author Dzmitry_Naskou
   */
  static class CountExecution extends OrientQueryExecution {

    /**
     * Instantiates a new {@link CountExecution}.
     *
     * @param template the template
     */
    public CountExecution(OrientOperations template, OrientParameters parameters) {
      super(template, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
      return operations.count(query.toSql(values), prepareParameters(parameters, values));
    }
  }

  /**
   * Executes the {@link AbstractOrientQuery} to return a {@link org.springframework.data.domain.Page} of entities.
   *
   * @author Dzmitry_Naskou
   */
  static class PagedExecution extends OrientQueryExecution {

    /**
     * Instantiates a new {@link PagedExecution}.
     *
     * @param template   the orient object template
     * @param parameters the parameters
     */
    public PagedExecution(OrientOperations template, OrientParameters parameters) {
      super(template, parameters);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.repository.object.query.OrientQueryExecution#doExecute(org.springframework.data.orient.repository.object.query.AbstractOrientQuery, java.lang.Object[])
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
      OrientParameterAccessor accessor = new OrientParametersParameterAccessor(parameters, values);

      final Object[] queryParams = prepareParameters(parameters, values);

      Long total = operations.count(query.toSqlCount(values), queryParams);

      Pageable pageable = accessor.getPageable();

      List<Object> content;

      if (pageable != null && total > pageable.getOffset()) {
        content = operations.query(query.toSql(values), mode, queryParams);
      } else {
        content = Collections.emptyList();
      }

      return new PageImpl<>(content, pageable, total);
    }
  }

  static class DeleteExecution extends OrientQueryExecution {

    public DeleteExecution(OrientOperations template, OrientParameters parameters) {
      super(template, parameters);
    }

    @Override
    protected Object doExecute(AbstractOrientQuery query, DetachMode mode, Object[] values) {
      List<OResult> ops = (List<OResult>) operations.command(query.toSql(values), prepareParameters(parameters, values));
      return ops.get(0).getProperty("count");
    }
  }
}
