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
 * Route metadata for all route methods within the class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Routes
{
  /**
   * <p>
   * The base matching URI path. If provided this value will be prepended to all {@link Route#value()} paths.
   * </p>
   */
  String value() default "";

  /**
   * <p>
   * If provided this value will be prepended to all forward paths returned by routes of the annotated class.
   * </p>
   *
   * @see org.baswell.routes.RoutesConfiguration#rootForwardPath
   */
  String forwardPath() default "";

  /**
   * <p>
   * The default type of media all routes in this class know how to serve. Overridden by {@link Route#acceptTypePatterns()}.
   * </p>
   */
  String[] acceptTypePatterns() default {};

  /**
   * <p>
   * Should public methods be candidates for HTTP routes if they aren't annotated with {@link org.baswell.routes.Route}?
   * If true only unannotated, public methods of the immediate class will be used. Public, unannotated methods of extended
   * classes will not be candidates (ex. {@link java.lang.Object#equals(Object)}. If this attribute is not explicitly set
   * then the {@link org.baswell.routes.RoutesConfiguration#routeUnannotatedPublicMethods}  will be used.
   * </p>
   *
   * <p>
   * This should be used as a single value. Only the first value in the array will be used.
   * </p>
   *
   * @see org.baswell.routes.RoutesConfiguration#routeUnannotatedPublicMethods
   */
  boolean[] routeUnannotatedPublicMethods() default {};

  /**
   * <p>
   * Default content type returned for routes in this class. This can be overridden in {@link Route#contentType()}, or by
   * explicitly calling {@link javax.servlet.http.HttpServletResponse#setContentType(String)} in the route method.
   * </p>
   */
  String defaultContentType() default "";
  
  /**
   *
   * <p>
   * If true by default strings returned from routes of this annotated class are sent back as the content. If false by default
   * returned strings are interpreted as file paths that the request is forwarded to. This can be overridden in
   * {@link Route#returnedStringIsContent()}.
   * </p>
   *
   * <p>
   * This should be used as a single value. Only the first value in the array will be used.
   * </p>
   *
   * @see org.baswell.routes.RoutesConfiguration#defaultReturnedStringIsContent
   * @see org.baswell.routes.Route#returnedStringIsContent()
   */
  boolean[] defaultReturnedStringIsContent() default {};

  /**
   * <p>
   * Tags to apply to all routes within the annotated class. If a route method defines its own tags, they will be added to
   * the tags defined here. Tags defined here will be added to any tags defined explicitly in {@link org.baswell.routes.Route#tags()}.
   * </p>
   *
   * @see BeforeRoute#exceptTags()
   * @see BeforeRoute#onlyTags()
   * @see AfterRoute#exceptTags()
   * @see AfterRoute#onlyTags()
   */
  String[] tags() default {};

  /**
   * Are routes within this class cacheable?
   */
  boolean[] cacheable() default {};
}
