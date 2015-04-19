package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Base route method schema that provides an HTTP method implementation that takes the HTTP method names are taken from
 * the beginning of the method name. If the method name doesn't start with any HTTP methods then the methods
 * [GET, POST, PUT, DELETE] are used. For example:
 *
 * getMyResource() -> [GET]
 *
 * getPostMyResource() -> [GET, POST]
 *
 * getPostDeleteAnotherThing() -> [GET, POST, DELETE]
 *
 * doSomething() -> [GET, POST, PUT, DELETE]
 */
abstract public class BaseRouteFromMethodScheme implements RouteFromMethodScheme
{
  @Override
  public List<HttpMethod> getHttpMethods(Method method)
  {
    List<HttpMethod> httpMethods = new ArrayList<HttpMethod>();
    String methodName = method.getName().toLowerCase();
    while (!methodName.isEmpty())
    {
      if (methodName.startsWith("get"))
      {
        httpMethods.add(HttpMethod.GET);
        methodName = methodName.substring("get".length(), methodName.length());
      }
      else if (methodName.startsWith("post"))
      {
        httpMethods.add(HttpMethod.POST);
        methodName = methodName.substring("post".length(), methodName.length());
      }
      else if (methodName.startsWith("put"))
      {
        httpMethods.add(HttpMethod.PUT);
        methodName = methodName.substring("put".length(), methodName.length());
      }
      else if (methodName.startsWith("delete"))
      {
        httpMethods.add(HttpMethod.DELETE);
        methodName = methodName.substring("delete".length(), methodName.length());
      }
      else if (methodName.startsWith("head"))
      {
        httpMethods.add(HttpMethod.HEAD);
        methodName = methodName.substring("head".length(), methodName.length());
      }
      else
      {
        break;
      }
    }
    
    if (httpMethods.isEmpty())
    {
      httpMethods.add(HttpMethod.GET);
      httpMethods.add(HttpMethod.POST);
      httpMethods.add(HttpMethod.PUT);
      httpMethods.add(HttpMethod.DELETE);
    }

    
    return httpMethods;
  }

  public String removeRoutesControllerHandlerFromName(Class clazz)
  {
    String className = clazz.getSimpleName();

    if (className.endsWith("Routes"))
    {
      className = className.substring(0, className.length() - "Routes".length());
    }
    else if (className.endsWith("Route"))
    {
      className = className.substring(0, className.length() - "Route".length());
    }
    else if (className.endsWith("Controller"))
    {
      className = className.substring(0, className.length() - "Controller".length());
    }
    else if (className.endsWith("Handler"))
    {
      className = className.substring(0, className.length() - "Handler".length());
    }

    if (className.equalsIgnoreCase("Root"))
    {
      return "";
    }
    else
    {
      return className;
    }
  }


  protected String removeHttpMethodsFromName(Method method)
  {
    Route route = method.getAnnotation(Route.class);
    if ((route == null) || (route.respondsToMethods().length == 0)) // Are we using the method name to determine the http methods ?
    {
      return removeHttpMethods(method.getName());
    }
    else
    {
      return method.getName();
    }
  }

  static String removeHttpMethods(String methodName)
  {
    if (methodName == null) return null;

    while (!methodName.isEmpty())
    {
      if (methodName.toLowerCase().startsWith("get"))
      {
        methodName = methodName.substring("get".length(), methodName.length());
      }
      else if (methodName.toLowerCase().startsWith("post"))
      {
        methodName = methodName.substring("post".length(), methodName.length());
      }
      else if (methodName.toLowerCase().startsWith("put"))
      {
        methodName = methodName.substring("put".length(), methodName.length());
      }
      else if (methodName.toLowerCase().startsWith("delete"))
      {
        methodName = methodName.substring("delete".length(), methodName.length());
      }
      else if (methodName.toLowerCase().startsWith("head"))
      {
        methodName = methodName.substring("head".length(), methodName.length());
      }
      else
      {
        break;
      }
    }
    return methodName;
  }



}
