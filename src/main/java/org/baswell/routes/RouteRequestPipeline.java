package org.baswell.routes;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.baswell.routes.invoking.RouteInvoker;
import org.baswell.routes.response.RouteResponseProcessor;

class RouteRequestPipeline
{
  final RoutesConfig routesConfig;
  
  final RouteResponseProcessor responseProcessor;
  
  RouteRequestPipeline(RoutesConfig routesConfig)
  {
    this.routesConfig = routesConfig;
    responseProcessor = new RouteResponseProcessor(routesConfig);
  }
  
  void invoke(RouteNode routeNode, HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpMethod httpMethod, Format format, RequestPath path, RequestParameters parameters) throws IOException, ServletException
  {
    RouteInvoker invoker = new RouteInvoker(servletRequest, servletResponse, httpMethod, path, parameters, format, routeNode.routeConfig);
    Object routeInstance = routeNode.instance.create();
    
    try
    {
      for (BeforeRouteNode beforeNode : routeNode.beforeRouteNodes)
      {
        Object beforeResponse = invoker.invoke(routeInstance, beforeNode.method, beforeNode.parameters);
        if (beforeNode.returnsBoolean && (beforeResponse != null) && (!(Boolean)beforeResponse))
        {
          return;
        }
      }

      Object response = invoker.invoke(routeInstance, routeNode.method, routeNode.parameters);
      responseProcessor.processResponse(routeNode.responseType, response, format, routeNode.routeConfig, servletRequest, servletResponse);
      
      for (AfterRouteNode afterNode : routeNode.afterRouteNodes)
      {
        invoker.invoke(routeInstance, afterNode.method, afterNode.parameters);
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
