package org.baswell.routes.response;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.baswell.routes.RouteConfig;

public class RouteResponseTypeMapper
{
  public RouteResponseType mapResponseType(Method method, RouteConfig routeConfig)
  {
    Class returnType = method.getReturnType();
    if ((returnType == void.class) ||(returnType == Void.class))
    {
      return RouteResponseType.VOID;
    }
    else if (returnType.isArray() && (returnType.getComponentType() == byte.class))
    {
      return RouteResponseType.BYTES_CONTENT;
    }
    else if (returnType == InputStream.class)
    {
      return RouteResponseType.STREAM_CONTENT;
    }
    else if ((returnType == String.class) && !routeConfig.responseIsBody)
    {
      return RouteResponseType.FORWARD_DISPATCH;
    }
    else
    {
      return RouteResponseType.STRING_CONTENT;
    }
  }

}
