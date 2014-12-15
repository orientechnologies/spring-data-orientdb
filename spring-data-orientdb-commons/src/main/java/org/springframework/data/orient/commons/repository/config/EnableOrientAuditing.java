package org.springframework.data.orient.commons.repository.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable auditing in OrientDB via annotation configuration.
 * 
 * @author Dzmitry_Naskou
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(OrientAuditingRegistrar.class)
public @interface EnableOrientAuditing {

	/**
	 * Configures the {@link org.springframework.data.domain.AuditorAware} bean to be used to lookup the current principal.
	 *
	 * @return
	 */
	String auditorAwareRef() default "";

	/**
	 * Configures whether the creation and modification dates are set. Defaults to {@literal true}.
	 *
	 * @return
	 */
	boolean setDates() default true;

	/**
	 * Configures whether the entity shall be marked as modified on creation. Defaults to {@literal true}.
	 *
	 * @return
	 */
	boolean modifyOnCreate() default true;

	/**
	 * Configures a {@link org.springframework.data.auditing.DateTimeProvider} bean name that allows customizing the {@link org.joda.time.DateTime} to be
	 * used for setting creation and modification dates.
	 * 
	 * @return
	 */
	String dateTimeProviderRef() default "";
}
