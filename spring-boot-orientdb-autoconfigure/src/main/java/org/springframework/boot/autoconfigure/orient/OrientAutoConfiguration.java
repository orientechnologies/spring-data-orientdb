package org.springframework.boot.autoconfigure.orient;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.commons.core.OrientDatabaseFactory;
import org.springframework.data.orient.commons.core.OrientTransactionManager;
import org.springframework.data.orient.commons.web.config.OrientWebConfigurer;
import org.springframework.data.orient.object.OrientObjectDatabaseFactory;
import org.springframework.data.orient.object.OrientObjectOperations;
import org.springframework.data.orient.object.OrientObjectTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnClass(OObjectDatabaseTx.class)
@EnableConfigurationProperties(OrientProperties.class)
public class OrientAutoConfiguration {

    @Autowired
    private OrientProperties properties;

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(OrientDatabaseFactory factory) {
        return new OrientTransactionManager(factory);
    }

    @Bean
    @ConditionalOnMissingBean(OrientObjectDatabaseFactory.class)
    public OrientObjectDatabaseFactory objectDatabaseFactory() {
        OrientObjectDatabaseFactory factory = new OrientObjectDatabaseFactory();

        configure(factory);

        return factory;
    }

    @Bean
    @ConditionalOnClass(OObjectDatabaseTx.class)
    @ConditionalOnMissingBean(OrientObjectOperations.class)
    public OrientObjectTemplate objectTemplate(OrientObjectDatabaseFactory factory) {
        return new OrientObjectTemplate(factory);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnClass(OObjectDatabaseTx.class)
    @ConditionalOnMissingBean(OrientWebConfigurer.class)
    public OrientWebConfigurer orientWebConfigurer() {
        return new OrientWebConfigurer();
    }

    @SuppressWarnings("rawtypes")
    protected void configure(OrientDatabaseFactory factory) {
        factory.setUrl(properties.getUrl());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        factory.setMaxPoolSize(properties.getMaxPoolSize());
    }

}
