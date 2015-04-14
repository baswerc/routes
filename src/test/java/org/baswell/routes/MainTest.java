package org.baswell.routes;

import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class MainTest extends EndToEndTest
{
  @Before
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
