package org.baswell.routes;

import java.io.InputStream;
import java.lang.reflect.Method;

class ResponseTypeMapper
{
  ResponseType mapResponseType(Method method, RouteConfiguration routeConfiguration)
  {
    Class returnType = method.getReturnType();
    if ((returnType == void.class) ||(returnType == Void.class))
    {
      return ResponseType.VOID;
    }
    else if (returnType.isArray() && (returnType.getComponentType() == byte.class))
    {
      return ResponseType.BYTES_CONTENT;
    }
    else if (returnType == InputStream.class)
    {
      return ResponseType.STREAM_CONTENT;
    }
    else if ((returnType == String.class) && !routeConfiguration.returnedStringIsContent)
    {
      return ResponseType.FORWARD_DISPATCH;
    }
    else
    {
      return ResponseType.STRING_CONTENT;
    }
  }

}
