package org.springframework.data.orient.commons.repository.annotation;

import java.lang.annotation.*;

/**
 * The annotation to declare custom queries directly on repository methods.
 *
 * @author Dzmitry_Naskou
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * Defines the Orient query to be executed when the annotated method is called.
     */
    String value() default "";

    /**
     * Defines a special count query that shall be used for pagination queries to lookup the total number of elements for
     * a page. If non is configured we will derive the count query from the method name.
     */
    boolean count() default false;
}
