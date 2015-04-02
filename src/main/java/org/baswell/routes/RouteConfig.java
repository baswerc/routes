package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.baswell.routes.criteria.InvalidRouteException;

public class RouteConfig
{
  public final String route;

  public final List<HttpMethod> httpMethods;
  
  public final Set<Format.Type> acceptedFormats;
  
  public final String contentType;
  
  public final boolean responseIsBody;

  public final Map<String, List<String>> defaultParameters;
  
  public final Set<String> tags;
  
  public final String forwardPath;

  public RouteConfig(Method method, RoutesConfig routesConfig)
  {
    this(method, routesConfig, method.getDeclaringClass().getAnnotation(Routes.class), method.getAnnotation(Route.class), 0);
  }

  public RouteConfig(Method method, RoutesConfig routesConfig, Routes routes, Route route, int routesPathIndex)
  {
    httpMethods = getHttpMethods(routes, route, method, routesConfig.routeFromMethodScheme);
    this.route = buildRoutePath(routesConfig.rootPath, routes, route, method, routesConfig.routeFromMethodScheme, routesPathIndex);
    acceptedFormats = new HashSet<Format.Type>();
    defaultParameters = new HashMap<String, List<String>>();

    tags = new HashSet<String>();
    if (routes != null)
    {
      tags.addAll(Arrays.asList(routes.tags()));
    }

    String forwardPath;
    if (routes != null)
    {
      acceptedFormats.addAll(Arrays.asList(routes.defaultAcceptedFormats()));

      forwardPath = routes.forwardPath();
      if (!forwardPath.startsWith("/"))
      {
        String rootForwardPath = routesConfig.rootForwardPath;
        if (rootForwardPath != null)
        {
          if (!rootForwardPath.endsWith("/")) rootForwardPath += "/";
          forwardPath = rootForwardPath + forwardPath;
        }
      }
      
    }
    else
    {
      forwardPath = routesConfig.rootForwardPath;
      if (forwardPath == null) forwardPath = "/";
    }

    if (!forwardPath.startsWith("/")) forwardPath = "/" + forwardPath;
    if (!forwardPath.endsWith("/")) forwardPath += "/";
    this.forwardPath = forwardPath;

    if (route == null)
    {
      contentType = ((routes == null) || (routes.defaultContentType().length() == 0)) ? routesConfig.defaultContentType : routes.defaultContentType();
      responseIsBody = ((routes == null) || (routes.defaultResponseIsBody().length == 0)) ? routesConfig.defaultResponseIsBody : routes.defaultResponseIsBody()[0];
    }
    else
    {
      if (route.contentType().length() == 0)
      {
        contentType = ((routes == null) || (routes.defaultContentType().length() == 0)) ? routesConfig.defaultContentType : routes.defaultContentType();
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
            throw new InvalidRouteException("Invalid default parameter: " + parameter + " for method: " + method);
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

      
      if (route.responseIsBody().length > 0)
      {
        responseIsBody = route.responseIsBody()[0];
      }
      else
      {
        responseIsBody = ((routes == null) || (routes.defaultResponseIsBody().length == 0)) ? routesConfig.defaultResponseIsBody : routes.defaultResponseIsBody()[0];
      }
      
      tags.addAll(Arrays.asList(route.tags()));
      acceptedFormats.addAll(Arrays.asList(route.acceptedFormats()));
    }
  }
  
  static String buildRoutePath(String rootPath, Routes routes, Route route, Method method, RouteFromMethodScheme routeFromMethodScheme, int routesPathIndex)
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
      String methodNameRoute = routeFromMethodScheme.getRoute(method);
      if (!methodNameRoute.isEmpty())
      {
        if (!routePath.isEmpty() && !routePath.endsWith("/")) routePath += "/";
        routePath += methodNameRoute;
      }
    }

    if (!routePath.startsWith("/")) routePath = "/" + routePath;
    return routePath;
  }

  static List<HttpMethod> getHttpMethods(Routes routes, Route route, Method method, RouteFromMethodScheme routeFromMethodScheme)
  {
    List<HttpMethod> httpMethods = new ArrayList<HttpMethod>();
    
    if ((route != null) && (route.httpMethods().length > 0))
    {
      for (HttpMethod httpMethod : route.httpMethods()) httpMethods.add(httpMethod);
    }
    else if ((routes != null) && (routes.defaultHttpMethods().length > 0))
    {
      for (HttpMethod httpMethod : routes.defaultHttpMethods()) httpMethods.add(httpMethod);
    }
    else
    {
      httpMethods.addAll(routeFromMethodScheme.getHttpMethods(method));
    }
    
    return httpMethods;
  }
}
