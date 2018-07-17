package org.springframework.data.orient.object.repository.support;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.orient.commons.repository.SourceType;
import org.springframework.data.orient.commons.repository.annotation.Cluster;
import org.springframework.data.orient.commons.repository.annotation.Source;
import org.springframework.data.orient.commons.repository.query.OrientQueryLookupStrategy;
import org.springframework.data.orient.commons.repository.support.OrientMetamodelEntityInformation;
import org.springframework.data.orient.commons.repository.support.SimpleOrientRepository;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.repository.OrientObjectRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;

import java.io.Serializable;
import java.util.Optional;

//TODO: find out why inheriting from OrientRepositoryFactory does not work; would save some code; but this here works
public class OrientObjectRepositoryFactory extends RepositoryFactorySupport {

  private final OrientObjectOperations operations;

  public OrientObjectRepositoryFactory(OrientObjectOperations operations) {
    super();
    this.operations = operations;
  }

  @Override
  public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> aClass) {
    return (EntityInformation<T, ID>) new OrientMetamodelEntityInformation<T>(aClass);
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Object getTargetRepository(RepositoryInformation metadata) {
    EntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
    Class<?> repositoryInterface = metadata.getRepositoryInterface();
    Class<?> javaType = entityInformation.getJavaType();
    String cluster = getCustomCluster(metadata);

    if (isObjectRepository(metadata.getRepositoryInterface())) {
      if (cluster != null) {
        return new SimpleOrientObjectRepository(operations, javaType, cluster, repositoryInterface);
      } else {
        return new SimpleOrientObjectRepository(operations, javaType, repositoryInterface);
      }
    } else {
      if (cluster != null) {
        return new SimpleOrientRepository(operations, javaType, cluster, repositoryInterface);
      } else {
        return new SimpleOrientRepository(operations, javaType, repositoryInterface);
      }
    }
  }

  @Override
  protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key,
      EvaluationContextProvider evaluationContextProvider) {
    return Optional.ofNullable(OrientQueryLookupStrategy.create(operations, key));
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    if (isObjectRepository(metadata.getRepositoryInterface())) {
      return SimpleOrientObjectRepository.class;
    } else {
      return SimpleOrientRepository.class;
    }
  }

  private boolean isObjectRepository(Class<?> repositoryInterface) {
    return OrientObjectRepository.class.isAssignableFrom(repositoryInterface);
  }

  /**
   * Get Custom Cluster Name. Method looks for {@link org.springframework.data.orient.commons.repository.annotation.Source} and
   * {@link org.springframework.data.orient.commons.repository.annotation.Cluster} annotation.
   * <p>
   * If {@link org.springframework.data.orient.commons.repository.annotation.Source} is not null and {@link
   * org.springframework.data.orient.commons.repository.annotation.Source#type()} equals to {@link
   * org.springframework.data.orient.commons.repository.SourceType#CLUSTER} then returns {@link
   * org.springframework.data.orient.commons.repository.annotation.Source#value()}
   * <p>
   * If {@link org.springframework.data.orient.commons.repository.annotation.Cluster} is not null then returns {@link
   * org.springframework.data.orient.commons.repository.annotation.Cluster#value()}
   *
   * @param metadata
   *
   * @return cluster name or null if it's not defined
   */
  private String getCustomCluster(RepositoryMetadata metadata) {
    Class<?> repositoryInterface = metadata.getRepositoryInterface();

    Source source = AnnotationUtils.getAnnotation(repositoryInterface, Source.class);
    if (source != null && SourceType.CLUSTER.equals(source.type())) {
      return source.value();
    }

    Cluster cluster = AnnotationUtils.getAnnotation(repositoryInterface, Cluster.class);
    if (cluster != null) {
      return cluster.value();
    }
    return null;
  }
}
