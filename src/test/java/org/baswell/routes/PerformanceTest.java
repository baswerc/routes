package org.baswell.routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.baswell.routes.TestMethods.getRequestParameters;
import static org.junit.Assert.assertTrue;

public class PerformanceTest {
    public static void main(String[] args) throws Exception {

        RequestPath path = new RequestPath(RequestPath.parseUrlSegments("/hello/world", "/app"));


        RouteData getPostRouteData = new RouteData(PerformanceTest.class, PerformanceTest.class.getMethod("getAndPost"), new RoutesConfiguration());

        List<CriterionForPathSegment> pathCriteria = createPathCriteria(Arrays.asList(CriterionForPathSegment.RequestPathSegmentCrierionType.FIXED, CriterionForPathSegment.RequestPathSegmentCrierionType.PATTERN, CriterionForPathSegment.RequestPathSegmentCrierionType.FIXED, CriterionForPathSegment.RequestPathSegmentCrierionType.PATTERN),
                Arrays.asList("one", ".*", "three", ".*"));

        RouteCriteria criteria = new RouteCriteria(Arrays.asList(HttpMethod.POST), pathCriteria, null, new ArrayList<>(), getPostRouteData, new RoutesConfiguration());

        RequestParameters requestParameters = getRequestParameters();

        RequestedMediaType requestedMediaType = new RequestedMediaType("text/html");
        criteria.matches(HttpMethod.POST, new RequestedMediaType("text/html"), path, requestParameters);
        path = new RequestPath(RequestPath.parseUrlSegments("/one/xxx/three/6", "/app"));
        criteria.matches(HttpMethod.POST, requestedMediaType, path, requestParameters);
        path = new RequestPath(RequestPath.parseUrlSegments("/one/vvv/three/6", "/app"));
        long start = System.nanoTime();
        boolean matches = criteria.matches(HttpMethod.POST, requestedMediaType, path, requestParameters);
        System.out.println(System.nanoTime() - start);
        System.out.println(matches);

    }

    static List<CriterionForPathSegment> createPathCriteria(List<CriterionForPathSegment.RequestPathSegmentCrierionType> types, List<String> values)
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
                    pathCriteria.add(new CriterionForPathSegment(i, values.get(i), CriterionForPathSegment.RequestPathSegmentCrierionType.PATTERN, Pattern.compile("^" + values.get(i) + "$")));
                    break;
            }
        }
        return pathCriteria;
    }

    @Route(methods = {HttpMethod.GET})
    public void getOnly()
    {}

    @Route(methods = {HttpMethod.GET, HttpMethod.POST})
    public void getAndPost()
    {}
}
