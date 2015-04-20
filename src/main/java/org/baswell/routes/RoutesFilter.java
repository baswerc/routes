package org.baswell.routes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  private volatile MethodPipeline pipeline;
  
  private List<Pattern> onlyPatterns;
  
  private List<Pattern> exceptPatterns;

  private volatile RoutesEntry routesEntry;
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    String onlyInitParam = filterConfig.getInitParameter("ONLY");
    if (onlyInitParam != null)
    {
      List<Pattern> onlyPatterns = new ArrayList<Pattern>();
      String[] onlyInitParams = onlyInitParam.split(",");
      for (String pattern : onlyInitParams)
      {
        if (!pattern.trim().isEmpty())
        {
          onlyPatterns.add(Pattern.compile(pattern));
        }
      }

      if (!onlyPatterns.isEmpty()) this.onlyPatterns = onlyPatterns;
    }

    String exceptInitParam = filterConfig.getInitParameter("EXCEPT");
    if (exceptInitParam != null)
    {
      List<Pattern> exceptPatterns = new ArrayList<Pattern>();
      String[] exceptInitParams = exceptInitParam.split(",");
      for (String pattern : exceptInitParams)
      {
        if (!pattern.trim().isEmpty())
        {
          exceptPatterns.add(Pattern.compile(pattern));
        }
      }

      if (!exceptPatterns.isEmpty()) this.exceptPatterns = exceptPatterns;
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
  {
    HttpServletRequest servletRequest = (HttpServletRequest)request;
    HttpServletResponse servletResponse = (HttpServletResponse)response;

    if (onlyPatterns != null || exceptPatterns != null)
    {
      String requestPath = servletRequest.getRequestURI().substring(servletRequest.getContextPath().length());

      if (onlyPatterns != null)
      {
        boolean matchFound = false;
        for (Pattern onlyPattern : onlyPatterns)
        {
          if (onlyPattern.matcher(requestPath).matches())
          {
            matchFound = true;
            break;
          }
        }

        if (!matchFound)
        {
          chain.doFilter(servletRequest, servletResponse);
          return;
        }
      }

      if (exceptPatterns != null)
      {
        for (Pattern exceptPattern : exceptPatterns)
        {
          if (exceptPattern.matcher(requestPath).matches())
          {
            chain.doFilter(servletRequest, servletResponse);
            return;
          }
        }
      }
    }

    assert theRoutingTable != null;

    if (routesEntry == null)
    {
      synchronized (this)
      {
        if (routesEntry == null)
        {
          routesEntry = new RoutesEntry(theRoutingTable);
        }
      }
    }

    if (!routesEntry.process(servletRequest, servletResponse))
    {
      chain.doFilter(servletRequest, servletResponse);
    }
  }

  @Override
  public void destroy()
  {}
}
