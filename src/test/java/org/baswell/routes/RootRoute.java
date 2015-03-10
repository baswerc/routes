package org.baswell.routes;

import org.baswell.routes.testroutes.BaseRoutes;

@Routes("/")
public class RootRoute extends BaseRoutes
{
  public void get()
  {
    methodsCalled.add("get");
  }
}
