package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Route
{
  /**
   * The route pattern.
   */
  String value() default "";

  HttpMethod[] httpMethods() default {};
  
  Format.Type[] acceptedFormats() default {};
  
  String contentType() default "";
  
  /**
   * Are Strings returned from routes sent directly to the client as the response?
   */
  boolean[] responseIsBody() default {};

  String[] defaultParameters() default {};
  
  String[] tags() default {};

}
