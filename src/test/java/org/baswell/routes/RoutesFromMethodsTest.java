package org.baswell.routes;

import java.io.IOException;

import javax.servlet.ServletException;

import org.baswell.routes.testroutes.RoutesFromMethods;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class RoutesFromMethodsTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    buildRoutingTable(RoutesFromMethods.class);
  }

  @Test
  public void testGetTest() throws IOException, ServletException
  {
    assertNotFound(new TestHttpServletRequest("POST", "/", "/test"));
    invoke(new TestHttpServletRequest("GET", "/", "/test"), "getTest");
  }

  @Test
  public void testGetPostTest2() throws IOException, ServletException
  {
    assertNotFound(new TestHttpServletRequest("DELETE", "/", "/test2"));
    invoke(new TestHttpServletRequest("POST", "/", "/test2"), "getPostTest2");
    invoke(new TestHttpServletRequest("GET", "/", "/test2"), "getPostTest2");
  }

  @Test
  public void testTest3() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("PUT", "/", "/test3"), "test3");
    invoke(new TestHttpServletRequest("DELETE", "/", "/test3"), "test3");
    invoke(new TestHttpServletRequest("POST", "/", "/test3"), "test3");
    invoke(new TestHttpServletRequest("GET", "/", "/test3"), "test3");
  }
}
