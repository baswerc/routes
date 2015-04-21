package org.baswell.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The entry point into the Routes engine. Used by {@link org.baswell.routes.RoutesFilter} and {@link org.baswell.routes.RoutesServlet}.
 */
public class RoutesEngine
{
  private final RoutingTable routingTable;

  private MethodPipeline pipeline;

  private MetaHandler metaHandler;

  /**
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
  }

  /**
   *
   * @param servletRequest
   * @param servletResponse
   * @return True if the request has been processed by the Routes engine. False if no match was found and the request has not been processed.
   * @throws IOException
   * @throws ServletException
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
    RequestFormat requestFormat = new RequestFormat(servletRequest.getHeader("Accept"), requestPath);

    MatchedRoute matchedRoute = null;

    if (routingTable.routesConfiguration.routesCache != null)
    {
      matchedRoute = (MatchedRoute) routingTable.routesConfiguration.routesCache.get(httpMethod, requestFormat, requestPath, requestParameters);
    }

    if (matchedRoute == null)
    {
      matchedRoute = routingTable.find(requestPath, requestParameters, httpMethod, requestFormat);
    }

    if (matchedRoute != null)
    {
      pipeline.invoke(matchedRoute.routeNode, servletRequest, servletResponse, httpMethod, requestFormat, requestPath, requestParameters, matchedRoute.pathMatchers, matchedRoute.parameterMatchers);

      if (routingTable.routesConfiguration.routesCache != null)
      {
        routingTable.routesConfiguration.routesCache.put(matchedRoute, httpMethod, requestFormat, requestPath, requestParameters);
      }

      return true;
    }
    else
    {
      return ((metaHandler != null) && metaHandler.handled(servletRequest, servletResponse, requestPath, requestParameters, httpMethod, requestFormat));
    }
  }
}
