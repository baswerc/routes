package org.baswell.routes.testroutes;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.baswell.routes.BeforeRoute;

public class BaseRoutes
{
  public Set<String> methodsCalled;

  public BaseRoutes()
  {}
  
  @BeforeRoute(order=0)
  public void setupMethodsCalled(HttpServletRequest request)
  {
    methodsCalled = new HashSet<String>();
    request.setAttribute("methodsCalled", methodsCalled);
  }

}
