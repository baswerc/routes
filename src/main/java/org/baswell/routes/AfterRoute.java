package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Callback to receive notification after HTTP requests have been routed. The response to the HTTP client has already
 * been sent by the time this callback is invoked.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AfterRoute
{
  /**
   * Do not execute after any routes that have matching {@link Route#tags()}.
   */
  String[] exceptTags() default {};

  /**
   * Only execute after any routes that have matching {@link Route#tags()}.
   */
  String[] onlyTags() default {};

  /**
   * An optional weight to control the order of method execution when there are multiple AfterRoute for a Route. Smaller
   * numbers go first.
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  int[] order() default {};
}
