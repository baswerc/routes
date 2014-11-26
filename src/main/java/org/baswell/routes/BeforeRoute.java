package org.baswell.routes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BeforeRoute
{
  String[] exceptTags() default {};

  String[] onlyTags() default {};
  
  int[] order() default {};
}
