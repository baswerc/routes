package org.baswell.routes;

import org.baswell.routes.utils.http.TestHttpServletRequest;
import org.baswell.routes.utils.http.TestHttpServletResponse;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class CacheTest {

    @Test
    public void testCached() throws IOException, ServletException {
        RoutesConfiguration routesConfiguration = new RoutesConfiguration();
        routesConfiguration.routeUnannotatedPublicMethods = true;

        RoutingTable routingTable = new RoutingTable(routesConfiguration);

        routingTable.add(StaticRoutes.class);
        routingTable.build();

        RoutingEngine routingEngine = new RoutingEngine(routingTable);
        routingEngine.process(new TestHttpServletRequest("GET", "", "/hello"), new TestHttpServletResponse());
        routingEngine.process(new TestHttpServletRequest("GET", "", "/hello"), new TestHttpServletResponse());

        Assert.assertNotNull(routesConfiguration.routeCache.get("/hello"));
    }

    @Test
    public void testCachedWithParameters() throws IOException, ServletException {
        RoutesConfiguration routesConfiguration = new RoutesConfiguration();
        routesConfiguration.routeUnannotatedPublicMethods = true;

        RoutingTable routingTable = new RoutingTable(routesConfiguration);

        routingTable.add(StaticRoutes.class);
        routingTable.build();

        RoutingEngine routingEngine = new RoutingEngine(routingTable);
        routingEngine.process(new TestHttpServletRequest("GET", "", "/hello", "hello", "world"), new TestHttpServletResponse());
        routingEngine.process(new TestHttpServletRequest("GET", "", "/hello", "hello", "world"), new TestHttpServletResponse());

        Assert.assertNotNull(routesConfiguration.routeCache.get("/hello?hello=world"));
    }

    @Routes("/")
    static public class StaticRoutes {

        @Route("hello")
        public void getHello() {

        }
    }
}

