package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class RouteConfiguration
{
  final String route;

  final List<HttpMethod> respondsToMethods;
  
  final Set<MediaType> respondsToMedia;
  
  String contentType;
  
  final boolean returnedStringIsContent;

  final Map<String, List<String>> defaultParameters;
  
  final Set<String> tags;
  
  final String forwardPath;

  RouteConfiguration(Class clazz, Method method, RoutesConfiguration routesConfiguration)
  {
    this(clazz, method, routesConfiguration, method.getDeclaringClass().getAnnotation(Routes.class), method.getAnnotation(Route.class), 0);
  }

  RouteConfiguration(Class clazz, Method method, RoutesConfiguration routesConfiguration, Routes routes, Route route, int routesPathIndex)
  {
    respondsToMethods = getHttpMethods(routes, route, method, routesConfiguration.routeFromMethodScheme);
    this.route = buildRoutePath(routesConfiguration.rootPath, routes, route, clazz, method, routesConfiguration.routeFromMethodScheme, routesPathIndex);
    respondsToMedia = new HashSet<MediaType>();
    defaultParameters = new HashMap<String, List<String>>();

    tags = new HashSet<String>();
    if (routes != null)
    {
      tags.addAll(Arrays.asList(routes.tags()));
    }

    String forwardPath;
    if (routes != null)
    {
      respondsToMedia.addAll(Arrays.asList(routes.defaultRespondsToMedia()));

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
              defaultParameters.put(vals[0], new ArrayList<String>());
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
      respondsToMedia.addAll(Arrays.asList(route.respondsToMedia()));
    }
  }
  
  static String buildRoutePath(String rootPath, Routes routes, Route route, Class routesClass, Method routeMethod, RouteFromMethodScheme routeFromMethodScheme, int routesPathIndex)
  {
    String routePath = "";
    if (rootPath != null)
    {
      routePath = rootPath;
    }
    
    if ((routes != null) && (routes.value().length > routesPathIndex) && !routes.value()[routesPathIndex].trim().isEmpty())
    {
      if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
      routePath += routes.value()[routesPathIndex].trim();
    }
    else if ((route == null) || route.value().trim().isEmpty())
    {
      if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
      routePath += routeFromMethodScheme.getRootPath(routesClass);
    }


    if ((route != null) && !route.value().trim().isEmpty())
    {
      String routeValue = route.value().trim();
      if (!routePath.isEmpty() && !routePath.endsWith("/") && !routeValue.startsWith("/")) routePath += "/";
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
      String methodNameRoutePath = routeFromMethodScheme.getHttpPath(routeMethod);
      if (!methodNameRoutePath.isEmpty())
      {
        if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
        routePath += methodNameRoutePath;
      }
    }

    if (!routePath.startsWith("/")) routePath = "/" + routePath;
    return routePath;
  }

  static List<HttpMethod> getHttpMethods(Routes routes, Route route, Method method, RouteFromMethodScheme routeFromMethodScheme)
  {
    List<HttpMethod> httpMethods = new ArrayList<HttpMethod>();
    
    if ((route != null) && (route.respondsToMethods().length > 0))
    {
      for (HttpMethod httpMethod : route.respondsToMethods()) httpMethods.add(httpMethod);
    }
    else
    {
      httpMethods.addAll(routeFromMethodScheme.getHttpMethods(method));
    }
    
    return httpMethods;
  }
}
