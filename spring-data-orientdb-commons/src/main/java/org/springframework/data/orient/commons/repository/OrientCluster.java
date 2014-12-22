package org.springframework.data.orient.commons.repository;


/**
 * Abstract interface for cluster information.
 * 
 * @author Dzmitry_Naskou
 */
public interface OrientCluster {

    /**
     * Gets the Orient cluster name.
     *
     * @return the name
     */
    String getName();
}
