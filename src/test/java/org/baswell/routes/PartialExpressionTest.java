package org.baswell.routes;

import org.baswell.routes.testroutes.PartialExpressionRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;

public class PartialExpressionTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    buildRoutingTable(PartialExpressionRoutes.class);
  }

  @Test
  public void testGetTest() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/test.pdf"), "getReport");
    assertEquals("test", PartialExpressionRoutes.reportName);
    assertEquals("pdf", PartialExpressionRoutes.extension);
  }
}
