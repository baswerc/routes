package org.baswell.routes;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.baswell.routes.RoutesMethods.*;

class RoutesData implements Routes
{
  private final String path;

  private final String forwardPath;

  private final List<String> acceptTypePatterns;

  private final Boolean routeUnannotatedPublicMethods;

  private final String defaultContentType;

  private final Boolean defaultReturnedStringIsContent;

  private final List<String> tags;

  RoutesData(Class clazz)
  {
    List<Routes> routeses = getRoutesHierarchy(clazz);

    if (routeses.isEmpty())
    {
      path = "";
      forwardPath = null;
      acceptTypePatterns = new ArrayList<String>();
      routeUnannotatedPublicMethods = null;
      defaultContentType = null;
      defaultReturnedStringIsContent = null;
      tags = new ArrayList<String>();
    }
    else if (routeses.size() == 1)
    {
      Routes routes = routeses.get(0);
      path = routes.value();
      forwardPath = routes.forwardPath();
      acceptTypePatterns = Arrays.asList(routes.acceptTypePatterns());
      routeUnannotatedPublicMethods = routes.routeUnannotatedPublicMethods().length == 0 ? null : routes.routeUnannotatedPublicMethods()[0];
      defaultContentType = routes.defaultContentType();
      defaultReturnedStringIsContent = routes.defaultReturnedStringIsContent().length == 0 ? null : routes.defaultReturnedStringIsContent()[0];
      tags = Arrays.asList(routes.tags());
    }
    else
    {

      String path = "";
      String forwardPath = "";
      int forwardPathSegments = 0;

      for (int i = routeses.size() - 1; i >= 0; i--)
      {
        Routes routes = routeses.get(i);
        String pathSegment = routes.value();
        if (hasContent(pathSegment)) {
          if (!path.endsWith("/")) {
            path += "/";
          }
          if (pathSegment.startsWith("/")) {
            pathSegment = pathSegment.substring(1);
          }

          path += pathSegment;
        }

        String forwardPathSegment = routes.forwardPath();
        if (hasContent(forwardPathSegment))
        {
          if (forwardPathSegments > 0)
          {
            if (!forwardPath.endsWith("/"))
            {
              forwardPath += "/";
            }

            if (forwardPathSegment.startsWith("/"))
            {
              forwardPathSegment = forwardPathSegment.length() == 1 ? "" : forwardPathSegment.substring(1);
            }
          }

          forwardPath += forwardPathSegment;
          ++forwardPathSegments;
        }
      }

      this.path = path;
      this.forwardPath = forwardPath.isEmpty() ? null : forwardPath;

      List<String> acceptTypePatterns = new ArrayList<String>();
      Boolean routeUnannotatedPublicMethods = null;
      String defaultContentType = null;
      Boolean defaultReturnedStringIsContent = null;
      List<String> tags = new ArrayList<String>();

      for (Routes routes : routeses)
      {
        for (String acceptTypePattern : routes.acceptTypePatterns())
        {
          if (!acceptTypePatterns.contains(acceptTypePattern))
          {
            acceptTypePatterns.add(acceptTypePattern);
          }
        }

        if (routeUnannotatedPublicMethods == null && routes.routeUnannotatedPublicMethods().length > 0)
        {
          routeUnannotatedPublicMethods = routes.routeUnannotatedPublicMethods()[0];
        }

        if (defaultContentType == null && hasContent(routes.defaultContentType()))
        {
          defaultContentType = routes.defaultContentType();
        }

        if (defaultReturnedStringIsContent == null && routes.defaultReturnedStringIsContent().length > 0)
        {
          defaultReturnedStringIsContent = routes.defaultReturnedStringIsContent()[0];
        }

        for (String tag : routes.tags())
        {
          if (!tags.contains(tag))
          {
            tags.add(tag);
          }
        }
      }

      this.acceptTypePatterns = acceptTypePatterns;
      this.routeUnannotatedPublicMethods = routeUnannotatedPublicMethods;
      this.defaultContentType = defaultContentType;
      this.defaultReturnedStringIsContent = defaultReturnedStringIsContent;
      this.tags = tags;
    }
  }

  @Override
  public String value()
  {
    return path;
  }

  @Override
  public String forwardPath()
  {
    return forwardPath == null ? "" : forwardPath;
  }

  @Override
  public String[] acceptTypePatterns()
  {
    return acceptTypePatterns.toArray(new String[acceptTypePatterns.size()]);
  }

  @Override
  public boolean[] routeUnannotatedPublicMethods()
  {
    return routeUnannotatedPublicMethods == null ? new boolean[0] : new boolean[]{routeUnannotatedPublicMethods};
  }

  @Override
  public String defaultContentType()
  {
    return defaultContentType == null ? "" : defaultContentType;
  }

  @Override
  public boolean[] defaultReturnedStringIsContent()
  {
    return defaultReturnedStringIsContent == null ? new boolean[0] : new boolean[]{defaultReturnedStringIsContent};
  }

  @Override
  public String[] tags()
  {
    return tags.toArray(new String[tags.size()]);
  }

  @Override
  public Class<? extends Annotation> annotationType()
  {
    return Routes.class;
  }

  static List<Routes> getRoutesHierarchy(Class clazz)
  {
    List<Routes> routeses = new ArrayList<Routes>();
    if (clazz != null)
    {
      Routes routes = (Routes) clazz.getAnnotation(Routes.class);
      if (routes != null)
      {
        routeses.add(routes);
      }
      routeses.addAll(getRoutesHierarchy(clazz.getSuperclass()));
    }
    return routeses;
  }

  static List<String> expandRoutes(List<String[]> routesHiearchy)
  {
    List<String> routes = new ArrayList<String>();
    for (String[] routeSegments : routesHiearchy)
    {
      if (routeSegments.length > 0)
      {
        List<List<String>> expandedRoutesList = new ArrayList<List<String>>();
        for (String routeSegment : routeSegments)
        {
          if (hasContent(routeSegment))
          {
            List<String> route = new ArrayList<String>();
            if (routes.isEmpty())
            {
              route.add(routeSegment);
            }
            else
            {
              for (String expandedRoute : routes)
              {
                if (!expandedRoute.endsWith("/"))
                {
                  expandedRoute += "/";
                }

                if (routeSegment.startsWith("/"))
                {
                  routeSegment = routeSegment.length() == 1 ? "" : routeSegment.substring(1, routeSegment.length());
                }
                route.add(expandedRoute + routeSegment);
              }
            }
            expandedRoutesList.add(route);
          }
        }

        routes.clear();
        for (List<String> expandedRoutes : expandedRoutesList)
        {
          routes.addAll(expandedRoutes);
        }
      }
    }
    return routes;
  }
}
