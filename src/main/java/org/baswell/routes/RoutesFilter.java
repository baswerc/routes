package org.baswell.routes;

import org.baswell.routes.cache.RoutesCache;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.baswell.routes.RoutingTable.*;

public class RoutesFilter implements Filter
{
  private volatile RouteRequestPipeline pipeline;

  private RoutesCache cache;

  private Pattern onlyPattern;
  
  private Pattern exceptPattern;

  private MetaHandler webHandler;
  
  @Override
  public void init(FilterConfig config) throws ServletException
  {
    String only = config.getInitParameter("ONLY");
    if (only != null)
    {
      onlyPattern = Pattern.compile(only);
    }

    String except = config.getInitParameter("EXCEPT");
    if (except != null)
    {
      exceptPattern = Pattern.compile(except);
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    assert RoutingTable.routingTable != null;

    final HttpServletRequest servletRequest = (HttpServletRequest)request;
    final HttpServletResponse servletResponse = (HttpServletResponse)response;
    final String requestPath = servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());

    if ((onlyPattern != null) || exceptPattern != null)
    {
      if (((onlyPattern != null) && !onlyPattern.matcher(requestPath).matches()) || ((exceptPattern != null) && exceptPattern.matcher(requestPath).matches()))
      {
        chain.doFilter(servletRequest, servletResponse);
        return;
      }
    }
    
    if (pipeline == null)
    {
      synchronized (this)
      {
        if (pipeline == null)
        {
          RoutesConfig routesConfig = routingTable.routesConfig;

          pipeline = new RouteRequestPipeline(routesConfig);

          cache = routesConfig.getCache();

          if (routesConfig.hasRoutesMetaPath())
          {
            webHandler = new MetaHandler(routingTable, routesConfig);
          }
        }
      }
    }

    final HttpMethod httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    final Format format = new Format(servletRequest.getHeader("Accept"));
    final RequestPath path = new RequestPath(servletRequest);
    final RequestParameters parameters = new RequestParameters(servletRequest);

    RouteNode routeNode = null;
    boolean fromCache = true;

    if (cache != null)
    {
      routeNode = (RouteNode)cache.get(httpMethod, format, path, parameters);
    }

    if (routeNode == null)
    {
      fromCache = false;
      routeNode = routingTable.find(path, parameters, httpMethod, format);
    }

    if (routeNode != null)
    {
      pipeline.invoke(routeNode, servletRequest, servletResponse, httpMethod, format, path, parameters);
      if (!fromCache && (cache != null))
      {
        cache.put(routeNode, httpMethod, format, path, parameters);
      }
    }
    else
    {
      if ((webHandler == null ) || !webHandler.handled(servletRequest, servletResponse, path, parameters, httpMethod, format))
      {
        chain.doFilter(servletRequest, servletResponse);
      }
    }
  }

  @Override
  public void destroy()
  {}
}
