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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

class RouteData implements Route
{
  final String route;

  final List<HttpMethod> httpMethods;

  final List<String> acceptTypePatterns = new ArrayList<>();

  String contentType;

  final boolean returnedStringIsContent;

  final Map<String, List<String>> defaultParameters;

  final Set<String> tags;

  final String forwardPath;

  RouteData(Class clazz, Method method, RoutesConfiguration routesConfiguration)
  {
    this(clazz, method, routesConfiguration, new RoutesData(method.getDeclaringClass()), method.getAnnotation(Route.class));
  }

  RouteData(Class clazz, Method method, RoutesConfiguration routesConfiguration, Routes routes, Route route)
  {
    httpMethods = getHttpMethods(routes, route, method, routesConfiguration.routeByConvention);
    this.route = buildRoutePath(routesConfiguration.rootPath, routes, route, clazz, method, routesConfiguration.routeByConvention);
    defaultParameters = new HashMap<>();

    tags = new HashSet<>();
    if (routes != null)
    {
      tags.addAll(Arrays.asList(routes.tags()));
    }

    String forwardPath;
    if (routes != null)
    {
      acceptTypePatterns.addAll(Arrays.asList(routes.acceptTypePatterns()));

      forwardPath = routes.forwardPath();
      if (!forwardPath.startsWith("/"))
      {
        String rootForwardPath = routesConfiguration.rootForwardPath;
        if (rootForwardPath != null)
        {
          if (!rootForwardPath.endsWith("/")) rootForwardPath += "/";
          forwardPath = rootForwardPath + forwardPath;
        }
      }

    }
    else
    {
      forwardPath = routesConfiguration.rootForwardPath;
      if (forwardPath == null) forwardPath = "/";
    }

    if (!forwardPath.startsWith("/")) forwardPath = "/" + forwardPath;
    if (!forwardPath.endsWith("/")) forwardPath += "/";
    this.forwardPath = forwardPath;

    if (route == null)
    {
      contentType = ((routes == null) || (routes.defaultContentType().length() == 0)) ? routesConfiguration.defaultContentType : routes.defaultContentType();
      returnedStringIsContent = ((routes == null) || (routes.defaultReturnedStringIsContent().length == 0)) ? routesConfiguration.defaultReturnedStringIsContent : routes.defaultReturnedStringIsContent()[0];
    }
    else
    {
      acceptTypePatterns.addAll(Arrays.asList(route.acceptTypePatterns()));
      if (route.contentType().length() == 0)
      {
        contentType = ((routes == null) || (routes.defaultContentType().length() == 0)) ? routesConfiguration.defaultContentType : routes.defaultContentType();
      }
      else
      {
        contentType = route.contentType();
      }

      if (route.defaultParameters().length > 0)
      {
        for (String parameter : route.defaultParameters())
        {
          String[] vals = parameter.split("=");
          if (vals.length != 2)
          {
            throw new RoutesException("Invalid default parameter: " + parameter + " for method: " + method);
          }
          else
          {
            if (!defaultParameters.containsKey(vals[0]))
            {
              defaultParameters.put(vals[0], new ArrayList<>());
            }
            defaultParameters.get(vals[0]).add(vals[1]);
          }
        }
      }


      if (route.returnedStringIsContent().length > 0)
      {
        returnedStringIsContent = route.returnedStringIsContent()[0];
      }
      else
      {
        returnedStringIsContent = ((routes == null) || (routes.defaultReturnedStringIsContent().length == 0)) ? routesConfiguration.defaultReturnedStringIsContent : routes.defaultReturnedStringIsContent()[0];
      }

      tags.addAll(Arrays.asList(route.tags()));
    }
  }

  static String buildRoutePath(String rootPath, Routes routes, Route route, Class routesClass, Method routeMethod, RouteByConvention routeByConvention)
  {
    String routePath = "";
    if (rootPath != null)
    {
      routePath = rootPath;
    }

    if ((routes != null) && !routes.value().trim().isEmpty())
    {
      if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
      routePath += routes.value().trim();
    }
    else if ((route == null) || route.value().trim().isEmpty())
    {
      if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
      routePath += routeByConvention.routesPathPrefix(routesClass);
    }

    if ((route != null) && !route.value().trim().isEmpty())
    {
      String routeValue = route.value().trim();
      if (!routePath.isEmpty() && !routePath.endsWith("/") && !routeValue.startsWith("/") && !routeValue.startsWith("?")) routePath += "/";
      if (routePath.endsWith("/") && routeValue.startsWith("/"))
      {
        routePath += routeValue.substring(1);
      }
      else
      {
        routePath += routeValue;
      }
    }
    else
    {
      String methodNameRoutePath = routeByConvention.routePath(routeMethod);
      if (!methodNameRoutePath.isEmpty())
      {
        if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
        routePath += methodNameRoutePath;
      }
    }

    if (!routePath.startsWith("/")) routePath = "/" + routePath;
    return routePath;
  }

  static List<HttpMethod> getHttpMethods(Routes routes, Route route, Method method, RouteByConvention routeByConvention)
  {
    List<HttpMethod> httpMethods = new ArrayList<HttpMethod>();

    if ((route != null) && (route.methods().length > 0))
    {
      for (HttpMethod httpMethod : route.methods()) httpMethods.add(httpMethod);
    }
    else
    {
      httpMethods.addAll(routeByConvention.respondsToMethods(method));
    }

    return httpMethods;
  }

  @Override
  public String value() {
    return route;
  }

  @Override
  public HttpMethod[] methods() {
    return httpMethods.toArray(new HttpMethod[httpMethods.size()]);
  }

  @Override
  public String[] acceptTypePatterns() {
    return acceptTypePatterns.toArray(new String[acceptTypePatterns.size()]);
  }

  @Override
  public String contentType() {
    return contentType;
  }

  @Override
  public boolean[] returnedStringIsContent() {
    return new boolean[] {returnedStringIsContent};
  }

  @Override
  public String[] defaultParameters() {
    List<String> builder = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : defaultParameters.entrySet()) {
      for (String value : entry.getValue()) {
        builder.add(entry.getKey() + "=" + value);
      }
    }
    return builder.toArray(new String[builder.size()]);
  }

  @Override
  public String[] tags() {
    return tags.toArray(new String[tags.size()]);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return Route.class;
  }

  @Override
  public String toString() {
    return value();
  }
}
