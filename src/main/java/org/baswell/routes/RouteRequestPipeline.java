package org.baswell.routes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.baswell.routes.invoking.RouteInvoker;
import org.baswell.routes.response.RouteResponseProcessor;

class RouteRequestPipeline
{
  final RoutesConfiguration routesConfiguration;
  
  final RouteResponseProcessor responseProcessor;
  
  RouteRequestPipeline(RoutesConfiguration routesConfiguration)
  {
    this.routesConfiguration = routesConfiguration;
    responseProcessor = new RouteResponseProcessor(routesConfiguration);
  }
  
  void invoke(RouteNode routeNode, HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpMethod httpMethod,
              Format format, RequestPath path, RequestParameters parameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers) throws IOException, ServletException
  {
    RouteInvoker invoker = new RouteInvoker(servletRequest, servletResponse, httpMethod, path, parameters, format, routeNode.routeConfig);
    Object routeInstance = routeNode.instance.create();
    
    try
    {
      for (BeforeRouteNode beforeNode : routeNode.beforeRouteNodes)
      {
        Object beforeResponse = invoker.invoke(routeInstance, beforeNode.method, beforeNode.parameters, pathMatchers, parameterMatchers);
        if (beforeNode.returnsBoolean && (beforeResponse != null) && (!(Boolean)beforeResponse))
        {
          return;
        }
      }

      // If provided set the configured content type. A route method can override this by setting HttpServletResponse.setContentType since we do it before the call.
      if (routeNode.routeConfig.contentType != null)
      {
        servletResponse.setContentType(routeNode.routeConfig.contentType);
      }

      for (Map.Entry<String, List<String>> entry : routeNode.routeConfig.defaultParameters.entrySet())
      {
        if (!parameters.contains(entry.getKey()))
        {
          parameters.set(entry.getKey(), entry.getValue());
        }
      }

      Object response = invoker.invoke(routeInstance, routeNode.method, routeNode.parameters, pathMatchers, parameterMatchers);
      responseProcessor.processResponse(routeNode.responseType, response, format, routeNode.routeConfig, servletRequest, servletResponse);
      
      for (AfterRouteNode afterNode : routeNode.afterRouteNodes)
      {
        invoker.invoke(routeInstance, afterNode.method, afterNode.parameters, pathMatchers, parameterMatchers);
      }
    }
    catch (InvocationTargetException e)
    {
      Throwable targetException = e.getTargetException();
      
      if (targetException instanceof RedirectTo)
      {
        servletResponse.sendRedirect(((RedirectTo)targetException).redirectUrl);
      }
      else if (targetException instanceof ReturnHttpResponseCode)
      {
        servletResponse.setStatus(((ReturnHttpResponseCode)targetException).code);
      }
      else if (targetException instanceof RuntimeException)
      {
        throw (RuntimeException)targetException;
      }
      else if (targetException instanceof Error)
      {
        throw (Error)targetException;
      }
      else
      {
        throw new RuntimeException(targetException);
      }
    }
  }
}
