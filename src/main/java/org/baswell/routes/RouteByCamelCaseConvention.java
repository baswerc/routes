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
 * <p>
 * Uses camel case convention of method names to create new path segments.
 * </p>
 *
 * <p>For example</p>
 *
 * <table>
 *   <thead>
 *     <tr>
 *       <th style="text-align: left;padding-right: 10px;">Method Name</th>
 *       <th style="text-align: left;">Route Path</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td style="text-align: left;padding-right: 10px;">get</td>
 *       <td>/</td>
 *     </tr>
 *     <tr>
 *       <td style="text-align: left;padding-right: 10px;">getMyResource</td>
 *       <td>/my/resource</td>
 *     </tr>
 *     <tr>
 *       <td style="padding-right: 10px;">getPostMyResource</td>
 *       <td>/my/resource</td>
 *     </tr>
 *     <tr>
 *       <td style="padding-right: 10px;">getPostDeleteAnotherThingHere</td>
 *       <td>/another/thing/here</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * <p>
 * The same convention is used for generating the root path from the class name.
 * </p>
 *
 * <table>
 *   <thead>
 *     <tr>
 *       <th style="text-align: left;padding-right: 10px;">Class Name</th>
 *       <th style="text-align: left;">Route Root Path</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td style="text-align: left;padding-right: 10px;">ApiRoutes</td>
 *       <td>/api</td>
 *     </tr>
 *     <tr>
 *       <td style="text-align: left;padding-right: 10px;">UserProfileController</td>
 *       <td>/user/profile</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * @see org.baswell.routes.RoutesConfiguration#routeByConvention
 */
public class RouteByCamelCaseConvention extends RouteByHttpMethodNameConvention
{
  @Override
  public String routesPathPrefix(Class routesClass)
  {
    return camelCaseToPath(removeRoutesControllerHandlerFromName(routesClass));
  }

  @Override
  public String routePath(Method routeMethod)
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
