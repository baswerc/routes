package org.baswell.routes;

import static org.baswell.routes.MethodParametersBuilder.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.baswell.routes.MethodParameter.RouteMethodParameterType;

import org.junit.Test;

public class MethodParamtersBuilderTest
{
  @Test
  public void testGetRouteMapParameterType() throws Exception
  {
    class TestParameterClass
    {
      public void one(Map<String, List<String>> one)
      {
        
      }

      public void two(Map<String, String> two)
      {
        
      }

      public void three(Map three)
      {
        
      }

      public void four(Map<String, Integer> four)
      {
        
      }

      public void five(Map<Object, String> five)
      {
        
      }
    }
    
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("one", Map.class).getGenericParameterTypes()[0]), RouteMethodParameterType.PARAMETER_LIST_MAP);
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("two", Map.class).getGenericParameterTypes()[0]), RouteMethodParameterType.PARAMETER_MAP);
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("three", Map.class).getGenericParameterTypes()[0]), RouteMethodParameterType.PARAMETER_LIST_MAP);
    assertNull(getRouteMapParameterType(TestParameterClass.class.getMethod("four", Map.class).getGenericParameterTypes()[0]));
    assertNull(getRouteMapParameterType(TestParameterClass.class.getMethod("five", Map.class).getGenericParameterTypes()[0]));
  }
  
  @Test
  public void testBuildParameters() throws Exception
  {
    
    class RouteTest
    {
      public void testMethod(RequestParameters parameters, RequestPath path, RequestContext context, RequestFormat format, HttpServletRequest request, HttpServletResponse response, Map<String, String> paramMap, Map<String, List<String>> paramMapList)
      {}
      
      public void testInvalidMethod(HttpServletRequest request, HttpServletResponse response, boolean invalid)
      {}
      
      @Route("/test/{}/{}?one={}")
      public void routeOne(String one, boolean two, HttpServletRequest request, List<Integer> three)
      {}
    }
    
    MethodParametersBuilder builder = new MethodParametersBuilder();
    
    List<MethodParameter> parameters = builder.buildParameters(RouteTest.class.getMethod("testMethod", RequestParameters.class, RequestPath.class, RequestContext.class, RequestFormat.class, HttpServletRequest.class, HttpServletResponse.class, Map.class, Map.class));
    assertNotNull(parameters);
    assertEquals(parameters.size(), 8);
    assertEquals(parameters.get(0).type, RouteMethodParameterType.REQUEST_PARAMETERS);
    assertEquals(parameters.get(1).type, RouteMethodParameterType.REQUEST_PATH);
    assertEquals(parameters.get(2).type, RouteMethodParameterType.REQUEST_CONTEXT);
    assertEquals(parameters.get(3).type, RouteMethodParameterType.FORMAT);
    assertEquals(parameters.get(4).type, RouteMethodParameterType.SERVLET_REQUEST);
    assertEquals(parameters.get(5).type, RouteMethodParameterType.SERVLET_RESPONSE);
    assertEquals(parameters.get(6).type, RouteMethodParameterType.PARAMETER_MAP);
    assertEquals(parameters.get(7).type, RouteMethodParameterType.PARAMETER_LIST_MAP);
    
    try
    {
      builder.buildParameters(RouteTest.class.getMethod("testInvalidMethod", HttpServletRequest.class, HttpServletResponse.class, boolean.class));
      fail();
    }
    catch (RoutesException e)
    {}
    
    Parser parser = new Parser();
    Method routeMethod = RouteTest.class.getMethod("routeOne", String.class, boolean.class, HttpServletRequest.class, List.class);
    ParsedRouteTree routeTree = parser.parse(routeMethod.getAnnotation(Route.class).value());

    RoutesConfiguration routesConfiguration = new RoutesConfiguration();
    CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
    Criteria criteria = criteriaBuilder.buildCriteria(routeMethod, routeTree, null, routesConfiguration);

    parameters = builder.buildParameters(routeMethod, criteria);
    
    assertEquals(parameters.size(), 4);
    assertEquals(parameters.get(0).type, RouteMethodParameterType.ROUTE_PATH);
    assertEquals((Integer)parameters.get(0).segmentIndex, (Integer)1);
    assertEquals(parameters.get(1).type, RouteMethodParameterType.ROUTE_PATH);
    assertEquals((Integer)parameters.get(1).segmentIndex, (Integer)2);
    assertEquals(parameters.get(2).type, RouteMethodParameterType.SERVLET_REQUEST);
    assertEquals(parameters.get(3).type, RouteMethodParameterType.ROUTE_PARAMETERS);
    
    
  }
  

}
