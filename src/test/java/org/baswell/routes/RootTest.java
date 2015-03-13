package org.baswell.routes;

import org.baswell.routes.testroutes.BaseRoutes;
import org.baswell.routes.testroutes.RoutesFromMethods;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class RootTest extends EndToEndTest
{
  @BeforeTest
  public void setupRoutingTable()
  {
    routesConfig = new RoutesConfig();
    routesConfig.routeUnannoatedPublicMethods = true;
    buildRoutingTable(RootRoute.class);
  }
  @Test
  public void testGet() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/"), "get");
    invoke(new TestHttpServletRequest("GET", "/", "/test"), "get");
  }

  @Test
  public void testOne() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/one"), "getOne");
    invoke(new TestHttpServletRequest("GET", "/", "/test/one"), "getOne");
  }

  @Routes({"/", "/test"})
  static public class RootRoute extends BaseRoutes
  {
    public void get()
    {
      methodsCalled.add("get");
    }

    public void getOne()
    {
      methodsCalled.add("getOne");
    }
  }
}
