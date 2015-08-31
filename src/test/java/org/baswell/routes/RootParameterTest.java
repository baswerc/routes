package org.baswell.routes;

import org.baswell.routes.testroutes.BaseRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class RootParameterTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    routesConfiguration = new RoutesConfiguration();
    buildRoutingTable(RootParametersRoute.class);
  }
  @Test
  public void testGet() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/test", "name", "one", "center", "two"), "get");
  }

  @Routes("/test")
  static public class RootParametersRoute extends BaseRoutes
  {
    @Route("?name={}&center={}")
    public void get(String name, String center)
    {
      assertEquals(name, "one");
      assertEquals(center, "two");
      methodsCalled.add("get");
    }
  }
}
