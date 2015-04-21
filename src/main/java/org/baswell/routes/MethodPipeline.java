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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class MethodPipeline
{
  final RoutesConfiguration routesConfiguration;
  
  final ResponseProcessor responseProcessor;

  final RoutesLogger logger;

  MethodPipeline(RoutesConfiguration routesConfiguration)
  {
    this.routesConfiguration = routesConfiguration;
    responseProcessor = new ResponseProcessor(routesConfiguration);
    logger = routesConfiguration.logger;
  }
  
  void invoke(RouteNode routeNode, HttpServletRequest servletRequest, HttpServletResponse servletResponse, HttpMethod httpMethod,
              RequestFormat requestFormat, RequestPath path, RequestParameters parameters, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers) throws IOException, ServletException
  {
    MethodInvoker invoker = new MethodInvoker(servletRequest, servletResponse, httpMethod, path, parameters, requestFormat, routeNode.routeConfiguration);
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
      if (routeNode.routeConfiguration.contentType != null)
      {
        servletResponse.setContentType(routeNode.routeConfiguration.contentType);
      }

      for (Map.Entry<String, List<String>> entry : routeNode.routeConfiguration.defaultParameters.entrySet())
      {
        if (!parameters.contains(entry.getKey()))
        {
          parameters.set(entry.getKey(), entry.getValue());
        }
      }

      Object response = invoker.invoke(routeInstance, routeNode.method, routeNode.parameters, pathMatchers, parameterMatchers);
      responseProcessor.processResponse(routeNode.responseType, routeNode.responseStringWriteStrategy, response, routeNode.routeConfiguration.contentType, routeNode.routeConfiguration, servletRequest, servletResponse);

      boolean success = getStatus(servletResponse) < 300;
      for (AfterRouteNode afterNode : routeNode.afterRouteNodes)
      {
        if ((afterNode.onlyOnSuccess && !success) || (afterNode.onlyOnError && success))
        {
          continue;
        }
        try
        {
          invoker.invoke(routeInstance, afterNode.method, afterNode.parameters, pathMatchers, parameterMatchers);
        }
        catch (Exception e)
        {
          if (logger != null)
          {
            logger.logError("AfterRoute method: " + afterNode.method + " threw exception.", e);
          }
        }
      }
    }
    catch (InvocationTargetException e)
    {
      Throwable targetException = e.getTargetException();
      boolean exceptionHandled = false;

      if (targetException instanceof RedirectTo)
      {
        servletResponse.sendRedirect(((RedirectTo)targetException).redirectUrl);
        exceptionHandled = true;
      }
      else if (targetException instanceof ReturnHttpResponseCode)
      {
        servletResponse.setStatus(((ReturnHttpResponseCode)targetException).code);
        exceptionHandled = true;
      }

      for (AfterRouteNode afterNode : routeNode.afterRouteNodes)
      {
        if (!afterNode.onlyOnSuccess)
        {
          try
          {
            invoker.invoke(routeInstance, afterNode.method, afterNode.parameters, pathMatchers, parameterMatchers);
          }
          catch (Exception exc)
          {
            if (logger != null)
            {
              logger.logError("AfterRoute method: " + afterNode.method + " threw exception.", exc);
            }
          }
        }
      }

      if (!exceptionHandled)
      {
        if (targetException instanceof RuntimeException)
        {
          throw (RuntimeException) targetException;
        }
        else if (targetException instanceof Error)
        {
          throw (Error) targetException;
        }
        else
        {
          throw new RuntimeException(targetException);
        }
      }
    }
    finally
    {
      if (routeNode.instance.createdFromFactory)
      {
        try
        {
          routeNode.instance.factory.doneUsing(routeInstance);
        }
        catch (Exception e)
        {}
      }
    }
  }

  static int getStatus(HttpServletResponse response)
  {
    try
    {
      Method method = response.getClass().getMethod("getStatus");
      return (Integer)method.invoke(response);
    }
    catch (Exception e)
    {
      return 0;
    }
  }
}
