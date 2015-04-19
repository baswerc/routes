package org.baswell.routes.testroutes;

import org.baswell.routes.Routes;

@Routes(routeUnannotatedPublicMethods =true)
public class LoginRoutes extends BaseRoutes
{
  public void post()
  {
    methodsCalled.add("post");
  }

  public void get()
  {
    methodsCalled.add("get");
  }
  
  public void getForgotPassword()
  {
    methodsCalled.add("getForgotPassword");
  }
}