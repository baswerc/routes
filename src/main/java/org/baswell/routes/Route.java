package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Route information for the annotated route method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Route
{
  /**
   * The route path pattern used to match HTTP requests to this route method. If not provided the route path pattern
   * will be built from the method name.
   */
  String value() default "";

  /**
   * The HTTP methods this route method accepts. If not provided the HTTP methods will be assigned from the method name.
   */
  HttpMethod[] httpMethods() default {};

  /**
   * The type for formats this route method accepts. If not provided the format of request will not be used as a match filter
   * for this route.
   */
  Format.Type[] acceptedFormats() default {};

  /**
   * The content type to set for this route. This can be overridden by calling {@link javax.servlet.http.HttpServletResponse#setContentType(String)}
   * from within the route method.
   */
  String contentType() default "";
  
  /**
   * Are Strings returned from routes sent directly to the client as the response?
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  boolean[] responseIsBody() default {};


  /**
   * Assign parameter values if the parameters are not present in the HTTP request. The default parameter values (when not explicitly provided) will be
   * used to match the route method to the HTTP request (if applicable) and will also be present in {@link org.baswell.routes.RequestParameters}.
   * They will be present in {@link javax.servlet.http.HttpServletRequest#getParameter(String)}.
   *
   * The format of this attribute array is {"parameter1Name=parameter1DefaultValue", "parameter2Name=parameter2DefaultValue"...}.
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
