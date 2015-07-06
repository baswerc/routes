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
import java.util.Set;

import javax.servlet.ServletException;

import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.baswell.routes.utils.http.TestHttpServletResponse;
import static org.junit.Assert.*;

abstract public class EndToEndTest
{
  protected RoutingTable routingTable;
  
  protected MethodPipeline pipeline;
  
  protected TestHttpServletRequest servletRequest;
  
  protected TestHttpServletResponse servletResponse;
  
  protected HttpMethod httpMethod;

  protected RequestedMediaType requestedMediaType;

  protected RequestContent requestContent;

  protected RequestPath path;
  
  protected RequestParameters parameters;

  protected RoutesConfiguration routesConfiguration;

  protected void buildRoutingTable(Object... routesInstancesClassesSymbolsOrPatterns)
  {
    routingTable = new RoutingTable(routesConfiguration);
    
    String symbolName = null;
    for (Object object : routesInstancesClassesSymbolsOrPatterns)
    {
      if (object instanceof String)
      {
        if (symbolName == null)
        {
          symbolName = (String)object;
        }
        else
        {
          routesConfiguration.defineSymbol(symbolName, (String)object);
          symbolName = null;
        }
      }
      else
      {
        routingTable.add(object);
      }
    }
    
    routingTable.build();
    pipeline = new MethodPipeline(routingTable.routesConfiguration);
  }
  
  protected void initializeRequest(TestHttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
    servletResponse = new TestHttpServletResponse();
    httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    requestedMediaType = new RequestedMediaType(servletRequest.getContentType(), new RequestPath(servletRequest), new RequestParameters(servletRequest));
    path = new RequestPath(servletRequest);
    parameters = new RequestParameters(servletRequest);

    requestContent = new RequestContent(routesConfiguration, servletRequest, String.class, MediaType.XML, "text/xml", new AvailableLibraries(routesConfiguration));
  }

  protected void assertNotFound(TestHttpServletRequest servletRequest)
  {
    initializeRequest(servletRequest);
    assertNull(find());
  }
  
  protected RequestedMediaType invoke(TestHttpServletRequest servletRequest, String... expectedMethodsCalled) throws IOException, ServletException
  {
    initializeRequest(servletRequest);
    invoke();
    if ((expectedMethodsCalled != null) && (expectedMethodsCalled.length > 0))
    {
      Set<String> methodsCalled = (Set<String>)servletRequest.getAttribute("methodsCalled");
      for (String expectedMethodCalled : expectedMethodsCalled)
      {
        assertTrue(methodsCalled.contains(expectedMethodCalled));
      }
      assertEquals(methodsCalled.size(), expectedMethodsCalled.length);
    }
    return null;
  }
  
  protected MatchedRoute find()
  {
    return routingTable.find(path, parameters, httpMethod, requestedMediaType);
  }
  
  protected void invoke() throws IOException, ServletException
  {
    MatchedRoute node = find();
    assertNotNull(node);
    try
    {
      pipeline.invoke(node.routeNode, servletRequest, servletResponse, httpMethod, requestedMediaType, path, parameters, requestContent, node.pathMatchers, node.parameterMatchers);
      servletResponse.writer.close();
    }
    catch (RouteInstanceBorrowException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  protected void assertMethodsCalled(String... methodNames)
  {
    Set<String> methodsCalled = (Set<String>)servletRequest.getAttribute("methodsCalled");
    assertNotNull(methodsCalled);
    for (String methodName : methodNames)
    {
      assertTrue(methodsCalled.contains(methodName));
    }
  }

  protected void assertMethodsNotCalled(String... methodNames)
  {
    Set<String> methodsCalled = (Set<String>)servletRequest.getAttribute("methodsCalled");
    assertNotNull(methodsCalled);
    for (String methodName : methodNames)
    {
      assertFalse(methodsCalled.contains(methodName));
    }
  }
}
