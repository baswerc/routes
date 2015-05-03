/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  @Test
  public void testGetUrl() throws IOException, ServletException
  {
    TestHttpServletRequest request = new TestHttpServletRequest("GET", "/", "/url");
    request.requestUrl = "http://localhost:8080/url";
    request.queryString = "one=1&two=1";

    invoke(request, "getUrl");
  }
}