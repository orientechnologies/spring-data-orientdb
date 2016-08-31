package org.springframework.data.orient.commons.repository.annotation;

import java.lang.annotation.*;

/**
 * The annotation to declare orient cluster.
 *
 * @author Dzmitry_Naskou
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cluster {

    /**
     * Defines the Orient cluster name.
     */
    String value();
}
