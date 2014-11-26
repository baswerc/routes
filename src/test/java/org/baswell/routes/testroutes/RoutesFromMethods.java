package org.baswell.routes.testroutes;

import org.baswell.routes.Routes;

@Routes(routeUnannoatedPublicMethods=true)
public class RoutesFromMethods extends BaseRoutes
{
  public void getTest()
  {
    methodsCalled.add("getTest");
  }

  public void getPostTest2()
  {
    methodsCalled.add("getPostTest2");
  }
  
  public void test3()
  {
    methodsCalled.add("test3");
  }
}