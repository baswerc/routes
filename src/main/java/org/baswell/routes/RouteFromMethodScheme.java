package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;

public interface RouteFromMethodScheme
{
  List<HttpMethod> getHttpMethods(Method method);

  String getRoute(Method method);
}
