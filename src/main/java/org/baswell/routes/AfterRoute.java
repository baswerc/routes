/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Callback to receive notification after HTTP requests have been routed. The response to the HTTP client has already
 * been sent by the time this callback is invoked.
 * </p>
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
   * Only call when the HTTP servlet response was successfully processed (no exceptions thrown and response status is less
   * than 300).
   */
  boolean[] onlyOnSuccess() default {};

  /**
   * Only call when the HTTP servlet response was unsuccessfully processed (an exception was thrown and response status is between
   * 200 and 299).
   */
  boolean[] onlyOnError() default {};

  /**
   * An optional weight to control the order of method execution when there are multiple AfterRoute for a Route. Smaller
   * numbers go first.
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  int[] order() default {};
}
