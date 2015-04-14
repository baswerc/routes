package org.baswell.routes;

import java.io.IOException;

import javax.servlet.ServletException;

import org.baswell.routes.testroutes.BasicRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BasicRoutesTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    buildRoutingTable(BasicRoutes.class);
  }
  
  @Test
  public void testOne() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/test", "/test/this/is/a/test", "one", "1", "two", "true", "authenticationAllowed", "true"), "requireAuthentication", "getTest");
  }

  @Test
  public void testNotAuthenticated() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/test", "/test/this/is/a/test", "one", "1", "two", "true", "authenticationAllowed", "false"), "requireAuthentication");
  }
  
  @Test
  public void testTwo() throws IOException, ServletException
  {
    assertNotFound(new TestHttpServletRequest("GET", "/test", "/test/sixty"));
    invoke(new TestHttpServletRequest("GET", "/test", "/test/60"), "getIntegerTest");
  }
  
  @Test
  public void testRedirectTo() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/test", "/test/redirect", "authenticationAllowed", "true"));
    assertEquals(servletResponse.redirect, "http://test.com/redirect");
  }

  @Test
  public void testNotFound() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/not_found", "authenticationAllowed", "true"));
    assertEquals(servletResponse.responseCode, (Integer)404);
  }
}