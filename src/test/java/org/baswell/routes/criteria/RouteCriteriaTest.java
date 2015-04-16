package org.baswell.routes.criteria;

import org.baswell.routes.RequestFormat;
import org.baswell.routes.HttpMethod;
import org.baswell.routes.RequestParameters;
import org.baswell.routes.RequestPath;
import org.baswell.routes.Route;
import org.baswell.routes.RouteConfiguration;
import org.baswell.routes.RoutesConfiguration;
import org.baswell.routes.criteria.RequestPathSegmentCriterion.RequestPathSegmentCrierionType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.baswell.routes.utils.RoutesMethods.*;
import static org.baswell.routes.utils.TestMethods.*;
import static org.junit.Assert.*;

public class RouteCriteriaTest
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
    List<RequestPathSegmentCriterion> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED),
                                                               Arrays.asList("a", "b", "c", "d"));
    
    RouteCriteria routeCriteria = new RouteCriteria(pathCriteria, null, getPostRouteConfiguration, new RoutesConfiguration());

    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c"));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "e"));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a"));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(new ArrayList<String>());
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));
  }
  
  @Test
  public void testExpression()
  {
    List<RequestPathSegmentCriterion> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "c", INTEGER_PATTERN));

    RouteCriteria routeCriteria = new RouteCriteria(pathCriteria, null, getRouteConfiguration, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "-1"));
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "0"));
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1.2"));
    assertFalse(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
  }

  @Test
  public void testMulti()
  {
    List<RequestPathSegmentCriterion> pathCriteria = createPathCriteria(Arrays.asList(RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.FIXED, RequestPathSegmentCrierionType.MULTI, RequestPathSegmentCrierionType.PATTERN),
                                                               Arrays.asList("a", "b", "**", INTEGER_PATTERN));

    RouteCriteria routeCriteria = new RouteCriteria(pathCriteria, null, getRouteConfiguration, new RoutesConfiguration());
    
    RequestPath urlPath = new RequestPath(Arrays.asList("a", "b", "c", "1"));
    RequestParameters requestParameters = getRequestParameters();
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "1"));
    requestParameters = getRequestParameters();
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d", "1"));
    requestParameters = getRequestParameters();
    assertTrue(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
    assertFalse(routeCriteria.matches(HttpMethod.POST, new RequestFormat("text/html"), urlPath, requestParameters));

    urlPath = new RequestPath(Arrays.asList("a", "b", "c", "d"));
    requestParameters = getRequestParameters();
    assertFalse(routeCriteria.matches(HttpMethod.GET, new RequestFormat("text/html"), urlPath, requestParameters));
  }

  
  List<RequestPathSegmentCriterion> createPathCriteria(List<RequestPathSegmentCrierionType> types, List<String> values)
  {
    List<RequestPathSegmentCriterion> pathCriteria = new ArrayList<RequestPathSegmentCriterion>();
    for (int i = 0; i < types.size(); i++)
    {
      switch (types.get(i))
      {
        case MULTI:
        case FIXED:
          pathCriteria.add(new RequestPathSegmentCriterion(i, values.get(i), types.get(i), null));
          break;
          
        case PATTERN:
          pathCriteria.add(new RequestPathSegmentCriterion(i, values.get(i), RequestPathSegmentCrierionType.PATTERN, Pattern.compile("^" + values.get(i) + "$")));
          break;
      }
    }
    return pathCriteria;
  }

}
