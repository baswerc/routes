package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Routes
{
  /**
   * The base matching URI path.
   */
  String[] value() default {};

  /**
   * If provided this value will be prepended to all forward paths returned.
   * 
   */
  String forwardPath() default "";

  /**
   * The default HTTP methods allowed for each method route in this class.
   */
  HttpMethod[] defaultHttpMethods() default {};
  
  /**
   * The default requested format allowed for each method route in this class.
   */
  Format.Type[] defaultAcceptedFormats() default {};

  /**
   * Should public methods of this class be candidates for HTTP routes if they aren't annotated with Route?
   */
  boolean routeUnannoatedPublicMethods() default false;

  
  /**
   * Default content type returned for routes in this class.
   */
  String defaultContentType() default "";
  
  /**
   * Are Strings returned from routes sent directly to the client as the response?
   */
  boolean[] defaultResponseIsBody() default {};

  String[] tags() default {};

}
