package org.baswell.routes;

public class RoutesHelpers
{
  public static String redirectTo(Class routesClass)
  {
    return "redirect:" + rootUrl(routesClass);
  }

  public static String rootUrl(Class routesClass)
  {
    Routes routes = new RoutesAggregate(routesClass);
    String[] values = routes.value();
    if (values != null && values.length > 0)
    {
      return values[0];
    }
    else
    {
      return RoutingTable.theRoutingTable.routesConfiguration.routeByConvention.routesPathPrefix(routesClass);
    }
  }
}
