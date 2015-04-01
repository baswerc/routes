package org.baswell.routes;

import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class MainTest extends EndToEndTest
{
  @BeforeTest
  public void setupRoutingTable()
  {
    routesConfig = new RoutesConfig();
    routesConfig.routeUnannoatedPublicMethods = true;
    buildRoutingTable(RoutesWithMain.class);
  }

  @Test
  public void testGet() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/"), "get");
  }
}
