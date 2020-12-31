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
 * Route metadata for a route method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Route
{
  /**
   * The route path pattern used to match HTTP requests to this route method. If not provided the route path pattern
   * will be built from the route method name.
   */
  String value() default "";

  /**
   * The HTTP methods this route method accepts. If not provided the allowed HTTP methods will be built from the route method
   * name.
   */
  HttpMethod[] methods() default {};

  /**
   * The type of media this route knows how to serve. If not provided the request media type will not be used as a criteria for matching this route.
   */
  String[] acceptTypePatterns() default {};

  /**
   * <p>
   * The content type to set in the response for this route. This can be overridden by calling {@link javax.servlet.http.HttpServletResponse#setContentType(String)}
   * from within the route method.
   * </p>
   *
   * @see org.baswell.routes.Routes#defaultContentType()
   */
  String contentType() default "";

  /**
   * <p>
   * Should the {@code String} returned from this method be sent directly to the client as the response? If the return type for this method is not a {@code String} this
   * attribute is ignored.
   * </p>
   *
   * <p>This should be used as a single value. Only the first value in the array will be used.</p>
   *
   * @see Routes#defaultReturnedStringIsContent()
   * @see org.baswell.routes.RoutesConfiguration#defaultReturnedStringIsContent
   */
  boolean[] returnedStringIsContent() default {};

  /**
   * <p>
   * Assign parameter values if the parameters are not present in the HTTP request. The default parameter values (when not explicitly provided) will be
   * used to match the route method to the HTTP request (if applicable) and will also be present in {@link org.baswell.routes.RequestParameters}.
   * They will not be present in {@link javax.servlet.http.HttpServletRequest#getParameter(String)}.
   * </p>
   *
   * <p>
   * The format of this attribute array is {"parameter1Name=parameter1DefaultValue", "parameter2Name=parameter2DefaultValue"...}.
   * </p>
   */
  String[] defaultParameters() default {};

  /**
   * The tags associated with this route.
   *
   * @see BeforeRoute#exceptTags()
   * @see BeforeRoute#onlyTags()
   * @see AfterRoute#exceptTags()
   * @see AfterRoute#onlyTags()
   *
   */
  String[] tags() default {};
}
