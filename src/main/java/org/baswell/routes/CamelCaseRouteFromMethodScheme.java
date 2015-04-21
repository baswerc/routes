/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
