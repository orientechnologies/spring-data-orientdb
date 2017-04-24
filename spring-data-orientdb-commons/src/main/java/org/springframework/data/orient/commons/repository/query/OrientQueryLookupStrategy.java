package org.springframework.data.orient.commons.repository.query;

import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;

public final class OrientQueryLookupStrategy {

    private OrientQueryLookupStrategy() {
        super();
    }

    public static QueryLookupStrategy create(OrientOperations operations, Key key) {
        if (key == null) {
            return new CreateIfNotFoundQueryLookupStrategy(operations);
        }

        switch (key) {
            case CREATE:
                return new CreateQueryLookupStrategy(operations);
            case USE_DECLARED_QUERY:
                return new DeclaredQueryLookupStrategy(operations);
            case CREATE_IF_NOT_FOUND:
                return new CreateIfNotFoundQueryLookupStrategy(operations);
            default:
                throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
        }
    }

    private abstract static class AbstractQueryLookupStrategy implements QueryLookupStrategy {

        private final OrientOperations operations;

        public AbstractQueryLookupStrategy(OrientOperations template) {
            this.operations = template;
        }

        /*
         * (non-Javadoc)
         *
         * @see org.springframework.data.repository.query.QueryLookupStrategy#
         * resolveQuery(java.lang.reflect.Method,
         * org.springframework.data.repository.core.RepositoryMetadata,
         * org.springframework.data.repository.core.NamedQueries)
         */
        public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
            return resolveQuery(new OrientQueryMethod(method, metadata,factory), operations, namedQueries);
        }

        protected abstract RepositoryQuery resolveQuery(OrientQueryMethod method, OrientOperations template, NamedQueries namedQueries);
    }

    private static class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

        /**
         * Instantiates a new {@link CreateQueryLookupStrategy} lookup strategy.
         *
         * @param template the application database service
         */
        public CreateQueryLookupStrategy(OrientOperations template) {
            super(template);
        }

        /* (non-Javadoc)
         * @see com.epam.e3s.data.repository.query.E3sQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(com.epam.e3s.data.repository.query.E3sQueryMethod, com.epam.e3s.core.db.AppDbService, org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(OrientQueryMethod method, OrientOperations operations, NamedQueries namedQueries) {
            try {
                return new PartTreeOrientQuery(method, operations);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Could not create query metamodel for method %s!", method.toString()), e);
            }
        }
    }

    private static class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {

        /**
         * Instantiates a new {@link DeclaredQueryLookupStrategy} lookup strategy.
         *
         * @param template the application database service
         */
        public DeclaredQueryLookupStrategy(OrientOperations template) {
            super(template);
        }

        /* (non-Javadoc)
         * @see com.epam.e3s.data.repository.query.E3sQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(com.epam.e3s.data.repository.query.E3sQueryMethod, com.epam.e3s.core.db.AppDbService, org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(OrientQueryMethod method, OrientOperations template, NamedQueries namedQueries) {
            String query = method.getAnnotatedQuery();

            if (query != null) {
                return new StringBasedOrientQuery(query, method, template);
            }

            throw new IllegalStateException(String.format("Did neither find a NamedQuery nor an annotated query for method %s!", method));
        }
    }

    private static class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

        /**
         * The declared query strategy.
         */
        private final DeclaredQueryLookupStrategy strategy;

        /**
         * The create query strategy.
         */
        private final CreateQueryLookupStrategy createStrategy;

        /**
         * Instantiates a new {@link CreateIfNotFoundQueryLookupStrategy} lookup strategy.
         *
         * @param db the application database service
         */
        public CreateIfNotFoundQueryLookupStrategy(OrientOperations db) {
            super(db);
            this.strategy = new DeclaredQueryLookupStrategy(db);
            this.createStrategy = new CreateQueryLookupStrategy(db);
        }

        /* (non-Javadoc)
         * @see com.epam.e3s.data.repository.query.E3sQueryLookupStrategy.AbstractQueryLookupStrategy#resolveQuery(com.epam.e3s.data.repository.query.E3sQueryMethod, com.epam.e3s.core.db.AppDbService, org.springframework.data.repository.core.NamedQueries)
         */
        @Override
        protected RepositoryQuery resolveQuery(OrientQueryMethod method, OrientOperations template, NamedQueries namedQueries) {
            try {
                return strategy.resolveQuery(method, template, namedQueries);
            } catch (IllegalStateException e) {
                return createStrategy.resolveQuery(method, template, namedQueries);
            }
        }
    }

}
