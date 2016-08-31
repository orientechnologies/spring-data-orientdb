package org.springframework.data.orient.commons.repository.annotation;

import org.springframework.data.orient.commons.repository.SourceType;

import java.lang.annotation.*;

/**
 * The annotation to declare orient source (class or cluster).
 *
 * @author Dzmitry_Naskou
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Source {

    /**
     * Defines the Orient source type. Defaults to {@link SourceType#CLUSTER}.
     *
     * @return the source type
     */
    SourceType type() default SourceType.CLUSTER;

    /**
     * Defines the Orient cluster name.
     */
    String value();
}
