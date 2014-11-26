package org.baswell.routes;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.baswell.routes.Format;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.RouteNode;
import org.baswell.routes.RouteRequestPipeline;
import org.baswell.routes.RoutingTable;
import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.baswell.routes.utils.http.TestHttpServletResponse;

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

  protected void buildRoutingTable(Object... routesInstancesClassesSymbolsOrPatterns)
  {
    routingTable = new RoutingTable();
    
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
          routingTable.defineSymbol(symbolName, (String)object);
          symbolName = null;
        }
      }
      else
      {
        routingTable.add(object);
      }
    }
    
    routingTable.build();
    pipeline = new RouteRequestPipeline(routingTable.routesConfig);
  }
  
  protected void initializeRequest(TestHttpServletRequest servletRequest)
  {
    this.servletRequest = servletRequest;
    servletResponse = new TestHttpServletResponse();
    httpMethod = HttpMethod.fromServletMethod(servletRequest.getMethod());
    format = new Format(servletRequest.getContentType());
    path = new RequestPath(servletRequest);
    parameters = new RequestParameters(servletRequest);
  }

  protected void assertNotFound(TestHttpServletRequest servletRequest)
  {
    initializeRequest(servletRequest);
    assertNull(find());
  }
  
  protected void invoke(TestHttpServletRequest servletRequest, String... expectedMethodsCalled) throws IOException, ServletException
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
  }
  
  protected RouteNode find()
  {
    return routingTable.find(path, parameters, httpMethod, format);
  }
  
  protected void invoke() throws IOException, ServletException
  {
    RouteNode node = find();
    assertNotNull(node);
    pipeline.invoke(node, servletRequest, servletResponse, httpMethod, format, path, parameters);
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
