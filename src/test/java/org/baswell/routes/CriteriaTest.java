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

import org.baswell.routes.CriterionForPathSegment.RequestPathSegmentCrierionType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.baswell.routes.RoutesMethods.*;
import static org.baswell.routes.TestMethods.*;
import static org.junit.Assert.*;

public class CriteriaTest
{
  RouteData getRouteData;

  RouteData getPostRouteData;

  @Route(methods = {HttpMethod.GET})
  public void getOnly()
  {}

  @Route(methods = {HttpMethod.GET, HttpMethod.POST})
  public void getAndPost()
  {}

  @Before
  public void setupTest() throws NoSuchMethodException
  {
    getRouteData = new RouteData(getClass(), getClass().getMethod("getOnly"), new RoutesConfiguration());
    getPostRouteData = new RouteData(getClass(), getClass().getMethod("getAndPost"), new RoutesConfiguration());
  }


  @Test
  public void testBasicPath()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED),
                                                               Arrays.asList("a", "b", "c", "d"));
    
    RouteCriteria criteria = new RouteCriteria(Arrays.asList(HttpMethod.POST), pathCriteria, null, new ArrayList<>(), getPostRouteData, new RoutesConfiguration());

    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c"));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "e"));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a"));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(new ArrayList<String>());
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));
  }
  
  @Test
  public void testExpression()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "c", INTEGER_PATTERN));

    RouteCriteria criteria = new RouteCriteria(Arrays.asList(HttpMethod.GET), pathCriteria, null, new ArrayList<>(), getRouteData, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "-1"));
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "0"));
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1.2"));
    assertFalse(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
  }

  @Test
  public void testMulti()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.MULTI, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "**", INTEGER_PATTERN));

    RouteCriteria criteria = new RouteCriteria(Arrays.asList(HttpMethod.GET), pathCriteria, null, new ArrayList<>(), getRouteData, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "1"));
    requestParameters = getRequestParameters();
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "1"));
    requestParameters = getRequestParameters();
    assertTrue(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
    assertFalse(criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    requestParameters = getRequestParameters();
    assertFalse(criteria.matches(HttpMethod.GET, new RequestedMediaType("text/html"), urlPath, requestParameters));
  }

  
  List<CriterionForPathSegment> createPathCriteria(List<RequestPathSegmentCrierionType> types, List<String> values)
  {
    List<CriterionForPathSegment> pathCriteria = new ArrayList<CriterionForPathSegment>();
    for (int i = 0; i < types.size(); i++)
    {
      switch (types.get(i))
      {
        case MULTI:
        case FIXED:
          pathCriteria.add(new CriterionForPathSegment(i, values.get(i), types.get(i), null));
          break;
          
        case PATTERN:
          pathCriteria.add(new CriterionForPathSegment(i, values.get(i), RequestPathSegmentCrierionType.PATTERN, Pattern.compile("^" + values.get(i) + "$")));
          break;
      }
    }
    return pathCriteria;
  }

}
