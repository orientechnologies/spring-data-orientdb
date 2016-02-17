package org.springframework.data.orient.commons.repository.annotation;

import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

/**
 * @author saljuama
 * @since 17th February 2016
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Persistent
public @interface Vertex {
}
