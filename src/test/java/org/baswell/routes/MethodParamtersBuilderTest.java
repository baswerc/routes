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

import static org.baswell.routes.MethodParametersBuilder.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("one", Map.class).getGenericParameterTypes()[0]), MethodRouteParameterType.PARAMETER_LIST_MAP);
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("two", Map.class).getGenericParameterTypes()[0]), MethodRouteParameterType.PARAMETER_MAP);
    assertEquals(getRouteMapParameterType(TestParameterClass.class.getMethod("three", Map.class).getGenericParameterTypes()[0]), MethodRouteParameterType.PARAMETER_LIST_MAP);
    assertNull(getRouteMapParameterType(TestParameterClass.class.getMethod("four", Map.class).getGenericParameterTypes()[0]));
    assertNull(getRouteMapParameterType(TestParameterClass.class.getMethod("five", Map.class).getGenericParameterTypes()[0]));
  }
  
  @Test
  public void testBuildParameters() throws Exception
  {
    
    class RouteTest
    {
      public void testMethod(RequestParameters parameters, RequestPath path, RequestedMediaType format, HttpServletRequest request, HttpServletResponse response, Map<String, String> paramMap, Map<String, List<String>> paramMapList)
      {}
      
      public void testInvalidMethod(HttpServletRequest request, HttpServletResponse response, boolean invalid)
      {}
      
      @Route("/test/{}/{}?one={}")
      public void routeOne(String one, boolean two, HttpServletRequest request, List<Integer> three)
      {}
    }
    
    MethodParametersBuilder builder = new MethodParametersBuilder();
    
    List<MethodParameter> parameters = builder.buildParameters(RouteTest.class.getMethod("testMethod", RequestParameters.class, RequestPath.class, RequestedMediaType.class, HttpServletRequest.class, HttpServletResponse.class, Map.class, Map.class));
    assertNotNull(parameters);
    assertEquals(parameters.size(), 7);
    assertEquals(parameters.get(0).type, MethodRouteParameterType.REQUEST_PARAMETERS);
    assertEquals(parameters.get(1).type, MethodRouteParameterType.REQUEST_PATH);
    assertEquals(parameters.get(2).type, MethodRouteParameterType.REQUESTED_MEDIA_TYPE);
    assertEquals(parameters.get(3).type, MethodRouteParameterType.SERVLET_REQUEST);
    assertEquals(parameters.get(4).type, MethodRouteParameterType.SERVLET_RESPONSE);
    assertEquals(parameters.get(5).type, MethodRouteParameterType.PARAMETER_MAP);
    assertEquals(parameters.get(6).type, MethodRouteParameterType.PARAMETER_LIST_MAP);
    
    try
    {
      builder.buildParameters(RouteTest.class.getMethod("testInvalidMethod", HttpServletRequest.class, HttpServletResponse.class, boolean.class));
      fail();
    }
    catch (RoutesException e)
    {}
    
    TreeParser treeParser = new TreeParser();
    Method routeMethod = RouteTest.class.getMethod("routeOne", String.class, boolean.class, HttpServletRequest.class, List.class);
    ParsedRouteTree routeTree = treeParser.parse(routeMethod.getAnnotation(Route.class).value());

    RoutesConfiguration routesConfiguration = new RoutesConfiguration();
    CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
    RouteCriteria criteria = criteriaBuilder.buildRouteCriteria(routeMethod, routeTree, new RouteData(getClass(), getClass().getMethod("getOnly"), new RoutesConfiguration()), routesConfiguration);

    parameters = builder.buildParameters(routeMethod, criteria);
    
    assertEquals(parameters.size(), 4);
    assertEquals(parameters.get(0).type, MethodRouteParameterType.ROUTE_PATH);
    assertEquals((Integer)parameters.get(0).segmentIndex, (Integer)1);
    assertEquals(parameters.get(1).type, MethodRouteParameterType.ROUTE_PATH);
    assertEquals((Integer)parameters.get(1).segmentIndex, (Integer)2);
    assertEquals(parameters.get(2).type, MethodRouteParameterType.SERVLET_REQUEST);
    assertEquals(parameters.get(3).type, MethodRouteParameterType.ROUTE_PARAMETERS);
    
    
  }

  @Route(methods = {HttpMethod.GET})
  public void getOnly()
  {}
}
