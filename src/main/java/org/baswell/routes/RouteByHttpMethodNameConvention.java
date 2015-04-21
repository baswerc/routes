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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * {@code RouteFromMethodScheme} implementation that provides {@code respondsToMethods}. The HTTP method names are taken from
 * the beginning of the method name. If the method name doesn't start with any HTTP methods then the methods [GET, POST, PUT, DELETE] are used.
 * </p>
 *
 * <p>For example:</p>
 *
 * <table>
 *   <thead>
 *     <tr>
 *       <th style="text-align: left;padding-right: 10px;">Method Name</th>
 *       <th>Route HTTP Methods</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td style="text-align: left;padding-right: 10px;">getMyResource</td>
 *       <td>GET</td>
 *     </tr>
 *     <tr>
 *       <td style="padding-right: 10px;">getPostMyResource</td>
 *       <td>GET, POST</td>
 *     </tr>
 *     <tr>
 *       <td style="padding-right: 10px;">getPostDeleteAnotherThing</td>
 *       <td>GET, POST, DELETE</td>
 *     </tr>
 *     <tr>
 *       <td style="padding-right: 10px;">doSomething</td>
 *       <td>GET, POST, PUT, DELETE</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
abstract public class RouteByHttpMethodNameConvention implements RouteByConvention
{
  /**
   * The HTTP method names are taken from the beginning of the method name. If the method name doesn't start with any HTTP methods then the methods [GET, POST, PUT, DELETE] are used.
   * @param method
   * @return The HTTP methods this method specifies from its name.
   */
  @Override
  public List<HttpMethod> respondsToMethods(Method method)
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

  /**
   * Removes any of the following words from the very end of the given class name.
   * <ul>
   *   <li>Routes</li>
   *   <li>Route</li>
   *   <li>Controller</li>
   *   <li>Handler</li>
   * </ul>
   *
   * @param clazz
   * @return The class name with the above words removed from the end.
   * @see RouteByConvention#routesPathPrefix(Class)
   */
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

  /**
   * Removes any of the HTTP method names GET, POST, PUT, DELETE, HEAD (case insensitive) from the beginning of this method
   * name if the given method does not have a {@link Route#respondsToMethods()} specified. If {@code respondsToMethods} is
   * specified the method name is returned unaltered.
   *
   * @param method
   * @return The method name minus the HTTP method names at the beginning.
   * @see RouteByConvention#routePath(java.lang.reflect.Method)
   */
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
