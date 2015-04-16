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
  RouteConfiguration getRouteConfiguration;

  RouteConfiguration getPostRouteConfiguration;

  @Route(httpMethods = {HttpMethod.GET})
  public void getOnly()
  {}

  @Route(httpMethods = {HttpMethod.GET, HttpMethod.POST})
  public void getAndPost()
  {}

  @Before
  public void setupTest() throws NoSuchMethodException
  {
    getRouteConfiguration = new RouteConfiguration(getClass().getMethod("getOnly"), new RoutesConfiguration());
    getPostRouteConfiguration = new RouteConfiguration(getClass().getMethod("getAndPost"), new RoutesConfiguration());
  }


  @Test
  public void testBasicPath()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED),
                                                               Arrays.asList("a", "b", "c", "d"));
    
    Criteria Criteria = new Criteria(pathCriteria, null, getPostRouteConfiguration, new RoutesConfiguration());

    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c"));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "e"));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a"));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(new ArrayList<String>());
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));
  }
  
  @Test
  public void testExpression()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "c", INTEGER_PATTERN));

    Criteria Criteria = new Criteria(pathCriteria, null, getRouteConfiguration, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "-1"));
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "0"));
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1.2"));
    assertFalse(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
  }

  @Test
  public void testMulti()
  {
    List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.MULTI, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "**", INTEGER_PATTERN));

    Criteria Criteria = new Criteria(pathCriteria, null, getRouteConfiguration, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "1"));
    requestParameters = getRequestParameters();
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "1"));
    requestParameters = getRequestParameters();
    assertTrue(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(Criteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    requestParameters = getRequestParameters();
    assertFalse(Criteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
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
