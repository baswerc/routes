package org.baswell.routes.testroutes;

import org.baswell.routes.Route;
import org.baswell.routes.Routes;

@Routes("/combined")
public class CombinedRoutes extends BaseRoutes
{
  @Route(value = "/one", responseIsBody = true)
  public String getOne()
  {
    methodsCalled.add("getOne");
    return "One";
  }
}
