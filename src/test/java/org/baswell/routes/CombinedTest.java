package org.baswell.routes;

import org.baswell.routes.testroutes.CombinedRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.testng.Assert.*;

public class CombinedTest extends EndToEndTest
{
  @BeforeTest
  public void setupRoutingTable()
  {
    buildRoutingTable(CombinedRoutes.class);
  }

  @Test
  public void testOne() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/combined/one"), "getOne");
    String response = new String(servletResponse.outputStream.toByteArray());
    assertEquals(response, "One");
  }

}
