package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * Scheme for mapping Methods without {@link org.baswell.routes.Route#value()} or {@link org.baswell.routes.Route#respondsToMethods()}
 *
 * @see DefaultRouteFromMethodScheme
 */
public interface RouteFromMethodScheme
{
  String getRootPath(Class routesClass);

  String getHttpPath(Method routeMethod);

  List<HttpMethod> getHttpMethods(Method method);
}
