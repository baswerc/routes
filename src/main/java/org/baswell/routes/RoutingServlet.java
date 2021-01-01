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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.baswell.routes.RoutingTable.*;

/**
 * <p>
 * An entry point for mapping HTTP servlet requests to route methods. All HTTP requests received by this servlet will be passed to
 * {@link RoutingEngine}. If a routes match was found the request will be processed by that route otherwise this
 * servlet will return a 404 (<status>HttpServletResponse.setStatus(404)</status>).
 * </p>
 *
 * @see RoutingFilter
 * @see RoutingEngine
 */
public class RoutingServlet extends HttpServlet
{
  private volatile static RoutingEngine routingEngine;

  @Override
  protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException
  {
    if (routingEngine == null)
    {
      synchronized (RoutingServlet.class)
      {
        if (routingEngine == null)
        {
          routingEngine = new RoutingEngine(theRoutingTable);
        }
      }
    }

    if (!routingEngine.process(servletRequest, servletResponse))
    {
      servletResponse.setStatus(404);
    }
  }
}