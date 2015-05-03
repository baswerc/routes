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
 * Callback to receive notification before HTTP requests have been sent to the route method. BeforeRoute methods should
 * return a {@code boolean} or {@code void}. If a {@code boolean} is the return type and {@code false} is returned, all
 * further processing will be stopped. A BeforeRoute method can also stop processing by throwing an exception.
 * </p>
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
   * <p>
   * An optional weight to control the order of method execution when there are multiple BeforeRoute for a Route. Smaller
   * numbers go first.
   * </p>
   *
   * <p>
   * This should be used as a single value. Only the first value in the array will be used.
   * </p>
   */
  int[] order() default {};
}
