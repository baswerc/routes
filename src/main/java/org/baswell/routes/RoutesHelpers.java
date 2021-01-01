package org.baswell.routes;

public class RoutesHelpers
{
  public static String redirectTo(Class routesClass)
  {
    return "redirect:" + rootUrl(routesClass);
  }

  public static String rootUrl(Class routesClass)
  {
    Routes routes = new RoutesData(routesClass);
    String path = routes.value();
    if (path.length() > 0)
    {
      return path;
    }
    else
    {
      return RoutingTable.theRoutingTable.routesConfiguration.routeByConvention.routesPathPrefix(routesClass);
    }
  }
}
