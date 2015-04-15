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

/**
 * Entry point for mapping HTTP servlet requests to route methods. This filter should be placed last in your filter chain.
 * No filters in the chain below this filter will be processed when route method matches are found (i.e. chain.doFilter is not called).
 * If no match is found, then chain.doFilter will be called so further processing can occur. This will allow, for example, to still
 * serve up file resources (ex. html, jsp) directly as long as none of your routes match the file resource URL.
 *
 * If your method routes are not the only mechanism for serving content, there are two parameters you can specify
 * to improve the performance of this filter.
 *
 * <code>
 * <init-param>
 *   <param-name>ONLY</param-name>
 *   <param-value>/routes.*</param-value>
 * </init-param>
 * </code>
 *
 * The <code>ONLY</code> parameter must be a valid Java regular expression. If specified, only request URIs that match
 * this pattern will be checked to see if they match any route methods. The URI matched against will not include the context
 * your application is deployed at so do not include that in the pattern.
 *
 * The other supported parameter is <code>EXCEPT</code>:
 *
 * <code>
 * <init-param>
 *   <param-name>EXCEPT</param-name>
 *   <param-value>.*\.jsp$</param-value>
 * </init-param>
 * </code>
 *
 * This parameter must also be a valid Java regular expression. If specified, all request URIs will be checked to see if they match
 * any route method except those that match this pattern. The URI matched against will not include the context
 * your application is deployed at so do not include that in the pattern.
 *
 * The <code>ONLY</code> and <code>EXCEPT</code> parameters are intended to be use exclusively (one or the other). If both
 * are specified then the route methods will be ignore if the <code>ONLY</code> pattern does not match or the <code>EXCEPT</code>
 * pattern does match.
 */
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
        return;
      }
    }
    
    if (pipeline == null)
    {
      synchronized (this)
      {
        if (pipeline == null)
        {
          pipeline = new RouteRequestPipeline(RoutingTable.routingTable.routesConfiguration);
        }

        if (RoutingTable.routingTable.routesConfiguration.hasRoutesMetaPath())
        {
          webHandler = new MetaHandler(RoutingTable.routingTable, RoutingTable.routingTable.routesConfiguration);
        }
      }
    }
    
    RequestPath path = new RequestPath(servletRequest);
    RequestParameters parameters = new RequestParameters(servletRequest);
    HttpMethod httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    Format format = new Format(servletRequest.getHeader("Accept"), path);

    MatchedRoute matchedRoute = RoutingTable.routingTable.find(path, parameters, httpMethod, format);
    if (matchedRoute != null)
    {
      pipeline.invoke(matchedRoute.routeNode, servletRequest, servletResponse, httpMethod, format, path, parameters, matchedRoute.pathMatchers, matchedRoute.parameterMatchers);
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
