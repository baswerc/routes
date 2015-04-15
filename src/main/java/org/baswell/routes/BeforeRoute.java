package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Callback to receive notification before HTTP requests have been sent to the route method. BeforeRoute methods can prevent
 * HTTP requests from being routed by returning false or throwing an exception.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BeforeRoute
{
  /**
   * Do not execute before any routes that have matching {@link Route#tags()}.
   */
  String[] exceptTags() default {};

  /**
   * Only execute before any routes that have matching {@link Route#tags()}.
   */
  String[] onlyTags() default {};

  /**
   * An optional weight to control the order of method execution when there are multiple BeforeRoute for a Route. Smaller
   * numbers go first.
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  int[] order() default {};
}
