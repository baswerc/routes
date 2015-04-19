package org.baswell.routes;

import java.lang.reflect.Method;

/**
 * Uses came case convention of method names to create new path segments. Removes all HTTP method names from the beginning of the method
 * name before performing path translation. For example:
 *
 * getMyResource() -> /my/resource
 *
 * getPostMyResource() -> /my/resource
 *
 * getPostDeleteAnotherThingHere() -> /another/thing/here
 *
 * For the HTTP methods, the HTTP method names are taken from the beginning of the method name. If the method name doesn't
 * start with any HTTP methods then the methods [GET, POST, PUT, DELETE] are used. For example:
 *
 * getMyResource() -> [GET]
 *
 * getPostMyResource() -> [GET, POST]
 *
 * getPostDeleteAnotherThing() -> [GET, POST, DELETE]
 *
 * doSomething() -> [GET, POST, PUT, DELETE]
 */
public class CamelCaseRouteFromMethodScheme extends BaseRouteFromMethodScheme
{
  @Override
  public String getRootPath(Class routesClass)
  {
    return camelCaseToPath(removeRoutesControllerHandlerFromName(routesClass));
  }

  @Override
  public String getHttpPath(Method routeMethod)
  {
    return camelCaseToPath(removeHttpMethodsFromName(routeMethod));
  }

  static String camelCaseToPath(String method)
  {
    if ((method == null) || method.trim().isEmpty()) return "";

    char[] chars = method.toCharArray();
    String path = Character.toString(Character.toLowerCase(chars[0]));
    for (int i = 1; i < chars.length; i++)
    {
      char c = chars[i];
      if (Character.isUpperCase(c))
      {
        path += "/";
      }
      path += Character.toLowerCase(c);
    }

    return path;
  }
}
