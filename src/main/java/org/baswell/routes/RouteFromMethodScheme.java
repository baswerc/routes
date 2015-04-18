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
  String getHttpPath(Method method);

  List<HttpMethod> getHttpMethods(Method method);
}
