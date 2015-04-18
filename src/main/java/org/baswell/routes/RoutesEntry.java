package org.baswell.routes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.baswell.routes.RoutingTable.theRoutingTable;

public class RoutesEntry
{
  private volatile MethodPipeline pipeline;

  private MetaHandler metaHandler;

  public boolean process(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException
  {
    assert theRoutingTable != null;

    if (!theRoutingTable.built)
    {
      synchronized (this)
      {
        if (!theRoutingTable.built)
        {
          theRoutingTable.build();
        }
      }
    }

    if (pipeline == null)
    {
      synchronized (this)
      {
        if (pipeline == null)
        {
          pipeline = new MethodPipeline(theRoutingTable.routesConfiguration);
        }

        if (theRoutingTable.routesConfiguration.hasRoutesMetaPath())
        {
          metaHandler = new MetaHandler(theRoutingTable, theRoutingTable.routesConfiguration);
        }
      }
    }

    RequestPath requestPath = new RequestPath(servletRequest);
    RequestParameters requestParameters = new RequestParameters(servletRequest);
    HttpMethod httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    RequestFormat requestFormat = new RequestFormat(servletRequest.getHeader("Accept"), requestPath);

    MatchedRoute matchedRoute = null;

    if (theRoutingTable.routesConfiguration.routesCache != null)
    {
      matchedRoute = (MatchedRoute)theRoutingTable.routesConfiguration.routesCache.get(httpMethod, requestFormat, requestPath, requestParameters);
    }

    if (matchedRoute == null)
    {
      matchedRoute = theRoutingTable.find(requestPath, requestParameters, httpMethod, requestFormat);
    }

    if (matchedRoute != null)
    {
      pipeline.invoke(matchedRoute.routeNode, servletRequest, servletResponse, httpMethod, requestFormat, requestPath, requestParameters, matchedRoute.pathMatchers, matchedRoute.parameterMatchers);

      if (theRoutingTable.routesConfiguration.routesCache != null)
      {
        theRoutingTable.routesConfiguration.routesCache.put(matchedRoute, httpMethod, requestFormat, requestPath, requestParameters);
      }

      return true;
    }
    else
    {
      return ((metaHandler != null ) && metaHandler.handled(servletRequest, servletResponse, requestPath, requestParameters, httpMethod, requestFormat));
    }
  }

}
