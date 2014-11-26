package org.baswell.routes;

import java.lang.reflect.Method;

public class SimpleRouteFromMethodScheme extends BaseRouteFromMethodScheme
{
  @Override
  public String getRoute(Method method)
  {
    return removeHttpMethodsFromName(method).toLowerCase();
  }
}
