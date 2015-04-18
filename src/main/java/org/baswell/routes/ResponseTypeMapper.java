package org.baswell.routes;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.baswell.routes.RoutesMethods.*;
import static org.baswell.routes.Pair.*;

class ResponseTypeMapper
{
  static ResponseType mapResponseType(Method method, RouteConfiguration routeConfiguration)
  {
    Class returnType = method.getReturnType();
    if ((returnType == void.class) || (returnType == Void.class))
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

  static Pair<ResponseStringWriteStrategy, String> mapResponseStringWriteStrategy(Method method, RequestFormat.Type requestFormatType, String contentType, AvailableLibraries availableLibraries)
  {
    Class returnType = method.getReturnType();
    String returnTypePackage = returnType.getPackage().toString();

    if (returnTypePackage.startsWith("org.json"))
    {
      return pair(ResponseStringWriteStrategy.TO_STRING, "application/json");
    }
    else if (returnTypePackage.startsWith("org.w3c.dom"))
    {
      return pair(ResponseStringWriteStrategy.W3C_NODE, "text/xml");
    }
    else if (returnTypePackage.startsWith("org.jdom2"))
    {
      return pair(ResponseStringWriteStrategy.JDOM, "text/xml");
    }
    else if (returnTypePackage.startsWith("org.dom4j"))
    {
      return pair(ResponseStringWriteStrategy.DOM4J, "text/xml");
    }
    else if (returnType.getAnnotation(XmlRootElement.class) != null)
    {
      return pair(ResponseStringWriteStrategy.JAXB, "text/xml");
    }
    else if (classImplementsInterface(returnType, CharSequence.class))
    {
      return pair(ResponseStringWriteStrategy.TO_STRING, contentType);
    }
    else if ((((requestFormatType != null) && (requestFormatType == RequestFormat.Type.JSON))
            || ((contentType != null) && contentType.contains("json")))
            && availableLibraries.gsonAvailable())
    {
      return pair(ResponseStringWriteStrategy.GSON, "application/json");
    }
    else
    {
      return pair(ResponseStringWriteStrategy.TO_STRING, null);
    }
  }
}


