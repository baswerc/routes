package org.baswell.routes;

import java.io.IOException;

import javax.servlet.ServletException;

import org.baswell.routes.testroutes.LoginRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

public class RoutesFromMethodsTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    buildRoutingTable(LoginRoutes.class);
  }

  @Test
  public void testRoot() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("POST", "/", "/login"), "post");
    invoke(new TestHttpServletRequest("GET", "/", "/login"), "get");
  }

  @Test
  public void testForgotPassword() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/login/forgotpassword"), "getForgotPassword");
  }
}
