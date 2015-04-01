package org.baswell.routes;

import org.baswell.routes.testroutes.BaseRoutes;

@Routes("/")
public class RoutesWithMain extends BaseRoutes
{
  public static void main(String[] args)
  {}

  public void get()
  {
    methodsCalled.add("get");
  }
}
