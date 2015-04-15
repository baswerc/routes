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
  
  protected RouteRequestPipeline pipeline;
  
  protected TestHttpServletRequest servletRequest;
  
  protected TestHttpServletResponse servletResponse;
  
  protected HttpMethod httpMethod;

  protected Format format;
  
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
    pipeline = new RouteRequestPipeline(routingTable.routesConfiguration);
  }
  
  protected void initializeRequest(TestHttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
    servletResponse = new TestHttpServletResponse();
    httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    format = new Format(servletRequest.getContentType(), new RequestPath(servletRequest));
    path = new RequestPath(servletRequest);
    parameters = new RequestParameters(servletRequest);
  }

  protected void assertNotFound(TestHttpServletRequest servletRequest)
  {
    initializeRequest(servletRequest);
    assertNull(find());
  }
  
  protected Format invoke(TestHttpServletRequest servletRequest, String... expectedMethodsCalled) throws IOException, ServletException
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
    return routingTable.find(path, parameters, httpMethod, format);
  }
  
  protected void invoke() throws IOException, ServletException
  {
    MatchedRoute node = find();
    assertNotNull(node);
    pipeline.invoke(node.routeNode, servletRequest, servletResponse, httpMethod, format, path, parameters, node.pathMatchers, node.parameterMatchers);
    servletResponse.writer.close();
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
