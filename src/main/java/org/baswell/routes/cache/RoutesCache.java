package org.baswell.routes.cache;

import org.baswell.routes.Format;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;

public interface RoutesCache
{
  Object get(HttpMethod method, Format format, RequestPath path, RequestParameters parameters);

  void put(Object routeNode, HttpMethod method, Format format, RequestPath path, RequestParameters parameters);
}
