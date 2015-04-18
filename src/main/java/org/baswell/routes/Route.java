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
   * will be built from the route method name.
   */
  String value() default "";

  /**
   * The HTTP methods this route method accepts. If not provided the allowed HTTP methods will be built from the route method
   * name.
   */
  HttpMethod[] respondsToMethods() default {};

  /**
   * The type of media this route knows how to serve. If not provided media will not be used as a criteria for matching HTTP
   * requests to routes.
   */
  MediaType[] respondsToMedia() default {};

  /**
   * The content type to set in the response for this route. This can be overridden by calling {@link javax.servlet.http.HttpServletResponse#setContentType(String)}
   * from within the route method.
   */
  String contentType() default "";

  /**
   * Are Strings returned from routes sent directly to the client as the response?
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  boolean[] returnedStringIsContent() default {};

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
