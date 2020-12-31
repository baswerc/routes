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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * <p>
 * Routes process engine. Reponsible for finding route method matches and executing matched methods. Used by {@link org.baswell.routes.RoutesFilter}
 * and {@link org.baswell.routes.RoutesServlet}.
 * </p>
 *
 * <p>
 * If no match is found on a call to {@code process} it is the responsibility of the caller to do something with the HTTP response. For example:
 * </p>
 *
 * <pre>
 * {@code
 * if (!routesEngine.process(servletRequest, servletResponse))
 * {
 *   servletResponse.setStatus(404);
 * }
 * }
 * </pre>
 */
public class RoutesEngine
{
  private final RoutingTable routingTable;

  private final MethodPipeline pipeline;

  private final MetaHandler metaHandler;

  private final RoutesLogger logger;

  /**
   * If the routingTable has not already been built the  {@link RoutingTable#build()} method will be called on the first
   * request received.
   *
   * @param routingTable
   */
  public RoutesEngine(RoutingTable routingTable)
  {
    assert routingTable != null;

    this.routingTable = routingTable;

    pipeline = new MethodPipeline(routingTable.routesConfiguration);
    if (routingTable.routesConfiguration.hasRoutesMetaPath())
    {
      metaHandler = new MetaHandler(routingTable, routingTable.routesConfiguration);
    }
    else
    {
      metaHandler = null;
    }

    logger = routingTable.routesConfiguration.logger;
  }

  /**
   * Looks for route matches for the given HTTP request. If a match is found the HTTP request is processed by the route method and <status>true</status> is returned.
   * Otherwise no action is performed on the request and <status>false</status> is returned.
   *
   * @param servletRequest The HTTP request.
   * @param servletResponse The HTTP response.
   * @return True if the request has been processed by the Routes engine. False if no match was found and the request has not been processed.
   * @throws IOException  If an input or output error occurs while the servlet is handling the HTTP request.
   * @throws ServletException If the HTTP request cannot be handled.
   */
  public boolean process(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException
  {
    assert servletRequest != null;
    assert servletResponse != null;

    if (!routingTable.built)
    {
      synchronized (this)
      {
        if (!routingTable.built)
        {
          routingTable.build();
        }
      }
    }

    RequestPath requestPath = new RequestPath(servletRequest);
    RequestParameters requestParameters = new RequestParameters(servletRequest);
    HttpMethod httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    RequestedMediaType requestedMediaType = new RequestedMediaType(servletRequest.getHeader("Accept"), requestPath, requestParameters);
    RequestContent requestContent = null;

    MatchedRoute matchedRoute = null;
    if (matchedRoute == null)
    {
      matchedRoute = routingTable.find(requestPath, requestParameters, httpMethod, requestedMediaType);
    }

    if (matchedRoute != null)
    {
      Type requestContentType = matchedRoute.routeNode.getRequestContentType();
      if (requestContentType != null)
      {
        requestContent = new RequestContent(routingTable.routesConfiguration, servletRequest);
      }

      try
      {
        pipeline.invoke(matchedRoute.routeNode, servletRequest, servletResponse, httpMethod, requestedMediaType, requestPath, requestParameters, requestContent, matchedRoute.pathMatchers, matchedRoute.parameterMatchers);

        return true;
      }
      catch (RouteInstanceBorrowException e)
      {
        if (logger != null)
        {
          logger.logError("Unable to create route instance for class: " + matchedRoute.routeNode.routeHolder.getRouteObjectClass(), e);
        }
        throw new ServletException(e);
      }
    }
    else
    {
      return ((metaHandler != null) && metaHandler.handled(servletRequest, servletResponse, requestPath, requestParameters, httpMethod, requestedMediaType));
    }
  }


}
