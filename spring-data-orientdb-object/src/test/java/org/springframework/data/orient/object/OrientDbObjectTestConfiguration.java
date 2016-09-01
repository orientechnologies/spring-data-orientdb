package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.commons.core.OrientTransactionManager;
import org.springframework.data.orient.commons.repository.config.EnableOrientRepositories;
import org.springframework.data.orient.object.domain.Address;
import org.springframework.data.orient.object.domain.Employee;
import org.springframework.data.orient.object.domain.Person;
import org.springframework.data.orient.object.repository.support.OrientObjectRepositoryFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.logging.LogManager;

@Configuration
@EnableTransactionManagement
@EnableOrientRepositories(basePackages = "org.springframework.data.orient.object",
        repositoryFactoryBeanClass = OrientObjectRepositoryFactoryBean.class)
public class OrientDbObjectTestConfiguration {

    public static final String EMPLOYEE_TMP_CLUSTER = "employee_tmp";
    public static final String EMPLOYEE_HISTORY_CLUSTER = "employee_history";

    static {
        // Note: trick for slf4j to manage log output of OrientLogManager (which uses JUL)
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    @Bean
    public OrientObjectDatabaseFactory factory() {
        OrientObjectDatabaseFactory factory = new OrientObjectDatabaseFactory();

        //factory.setUrl("plocal:target/spring-data-orientdb-db");
        factory.setUrl("memory:spring-data-orientdb-db");
        factory.setUsername("admin");
        factory.setPassword("admin");

        return factory;
    }

    @Bean
    public OrientTransactionManager transactionManager() {
        return new OrientTransactionManager(factory());
    }

    @Bean
    @Qualifier("employeeClusterTemplate")
    public OrientObjectTemplate objectTemplate() {
        return new OrientObjectTemplate(factory());
    }

    @PostConstruct
    public void registerEntities() {
        OObjectDatabaseTx db = factory().db();

        OEntityManager em = db.getEntityManager();
        em.registerEntityClass(Person.class);
        em.registerEntityClass(Address.class);
        em.registerEntityClass(Employee.class);

        if (!db.existsCluster(EMPLOYEE_TMP_CLUSTER)) {
            int id = db.addCluster(EMPLOYEE_TMP_CLUSTER);
            db.getMetadata().getSchema().getClass(Employee.class).addClusterId(id);
        }
        if (!db.existsCluster(EMPLOYEE_HISTORY_CLUSTER)) {
            int id = db.addCluster(EMPLOYEE_HISTORY_CLUSTER);
            db.getMetadata().getSchema().getClass(Employee.class).addClusterId(id);
        }

    }
}
