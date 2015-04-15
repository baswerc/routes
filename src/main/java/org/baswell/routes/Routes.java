package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Default route information for all route methods contained within the annotated class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Routes
{
  /**
   * The base matching URI paths. If provided these values will be prepended to all {@link Route#value()} paths. Multiple
   * values can be specified here here so that a routes class supports multiple root urls.
   */
  String[] value() default {};

  /**
   * If provided this value will be prepended to all forward paths returned by routes of the annotated class.
   */
  String forwardPath() default "";

  /**
   * The default requested format allowed for each method route in this class. Overridden by {@link Route#acceptedFormats()}.
   */
  Format.Type[] defaultAcceptedFormats() default {};

  /**
   * Should public methods be candidates for HTTP routes if they aren't annotated with {@link org.baswell.routes.Route}?
   * If true only unannotated, public methods of the immediate class will be used. Public, unannotated methods of extended
   * classes will not be candidates (ex. {@link java.lang.Object#equals(Object)}. If this attribute is not explicitly set
   * then the {@link org.baswell.routes.RoutesConfiguration#routeUnannoatedPublicMethods}  will be used.
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  boolean[] routeUnannoatedPublicMethods() default {};

  /**
   * Default content type returned for routes in this class.
   */
  String defaultContentType() default "";
  
  /**
   * If true, all strings returned from routes of this annotated class will be considered the  Are Strings returned from
   * routes sent directly to the client as the response?
   *
   * This should be used as a single value. Only the first value in the array will be used.
   */
  boolean[] defaultResponseIsBody() default {};

  /**
   * Tags to apply to all routes within the annotated class. If a route method defines its own tags, they will be added to
   * the tags defined here.
   *
   * @see BeforeRoute#exceptTags()
   * @see BeforeRoute#onlyTags()
   * @see AfterRoute#exceptTags()
   * @see AfterRoute#onlyTags()
   */
  String[] tags() default {};

}
