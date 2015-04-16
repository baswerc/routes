package org.baswell.routes.cache;

import org.baswell.routes.RequestFormat;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;

abstract public class BaseRoutesCache implements  RoutesCache
{
  protected String getKey(HttpMethod method, RequestFormat requestFormat, RequestPath path, RequestParameters parameters)
  {
    StringBuilder keyBuilder = new StringBuilder(method.toString()).append(':').append(requestFormat).append(':').append(path);
    if ((parameters != null) && parameters.hasParameters())
    {
      keyBuilder.append(':').append(parameters);
    }
    return keyBuilder.toString();
  }

}
