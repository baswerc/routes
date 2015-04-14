package org.baswell.routes;

import org.baswell.routes.testroutes.CombinedRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class CombinedTest extends EndToEndTest
{
  @Before
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
