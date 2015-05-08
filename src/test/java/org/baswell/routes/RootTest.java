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

import org.baswell.routes.testroutes.BaseRoutes;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.*;

public class RootTest extends EndToEndTest
{
  @Before
  public void setupRoutingTable()
  {
    routesConfiguration = new RoutesConfiguration();
    routesConfiguration.routeUnannotatedPublicMethods = true;
    buildRoutingTable(RootRoute.class);
  }
  @Test
  public void testGet() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/"), "get");
    invoke(new TestHttpServletRequest("GET", "/", "/test"), "get");
  }

  @Test
  public void testOne() throws IOException, ServletException
  {
    invoke(new TestHttpServletRequest("GET", "/", "/one"), "getOne");
    invoke(new TestHttpServletRequest("GET", "/", "/test/one"), "getOne");
  }

  static String expectedName;

  @Test
  public void testName() throws IOException, ServletException
  {

    expectedName = "blahblahblah";
    invoke(new TestHttpServletRequest("GET", "/", "/" + expectedName), "getName");
    expectedName = "abc";
    invoke(new TestHttpServletRequest("GET", "/", "/test/" + expectedName), "getName");
  }

  @Routes({"/", "/test"})
  static public class RootRoute extends BaseRoutes
  {
    @Route("*")
    public void getName(String name, RequestedMediaType requestedMediaType, HttpServletRequest request)
    {
      assertEquals(name, expectedName);
      methodsCalled.add("getName");
    }

    public void get()
    {
      methodsCalled.add("get");
    }

    public void getOne()
    {
      methodsCalled.add("getOne");
    }
  }
}
