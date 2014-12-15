package org.springframework.data.orient.commons.repository.config;

import org.springframework.data.orient.commons.repository.support.OrientRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

/**
 * {@link org.springframework.data.repository.config.RepositoryConfigurationExtension} for OrientDB.
 * 
 * @author Dzmitry_Naskou
 */
public class OrientRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtension#getRepositoryFactoryClassName()
     */
    public String getRepositoryFactoryClassName() {
        return OrientRepositoryFactoryBean.class.getName();
    }

    /* (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getModulePrefix()
     */
    @Override
    protected String getModulePrefix() {
        return "orient";
    }
}
