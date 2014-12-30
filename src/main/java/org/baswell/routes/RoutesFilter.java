package org.baswell.routes;

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

public class RoutesFilter implements Filter
{
  private volatile RouteRequestPipeline pipeline;
  
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

    HttpServletRequest servletRequest = (HttpServletRequest)request;
    HttpServletResponse servletResponse = (HttpServletResponse)response;
    
    if ((onlyPattern != null) || exceptPattern != null)
    {
      String requestPath = servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());
      if (((onlyPattern != null) && !onlyPattern.matcher(requestPath).matches()) || ((exceptPattern != null) && exceptPattern.matcher(requestPath).matches()))
      {
        chain.doFilter(servletRequest, servletResponse);
      }
    }
    
    if (pipeline == null)
    {
      synchronized (this)
      {
        if (pipeline == null)
        {
          pipeline = new RouteRequestPipeline(RoutingTable.routingTable.routesConfig);
        }

        if (RoutingTable.routingTable.routesConfig.hasMetaPath())
        {
          webHandler = new MetaHandler(RoutingTable.routingTable, RoutingTable.routingTable.routesConfig);
        }
      }
    }
    
    RequestPath path = new RequestPath(servletRequest);
    RequestParameters parameters = new RequestParameters(servletRequest);
    HttpMethod httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    Format format = new Format(servletRequest.getHeader("Accept"));

    RouteNode routeNode = RoutingTable.routingTable.find(path, parameters, httpMethod, format);
    if (routeNode != null)
    {
      pipeline.invoke(routeNode, servletRequest, servletResponse, httpMethod, format, path, parameters);
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
