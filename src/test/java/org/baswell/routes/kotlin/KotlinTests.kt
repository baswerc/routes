package org.baswell.routes.kotlin

import org.baswell.routes.RoutesConfiguration
import org.baswell.routes.RoutingEngine
import org.baswell.routes.RoutingTable
import org.baswell.routes.utils.http.TestHttpServletRequest
import org.baswell.routes.utils.http.TestHttpServletResponse
import org.junit.Assert
import org.junit.Test

class KotlinTests {
    @Test
    fun testObject() {
        val routesConfiguration = RoutesConfiguration()
        routesConfiguration.routeUnannotatedPublicMethods = true

        val routingTable = RoutingTable(routesConfiguration)

        routingTable.add(ObjectRoutes)
        routingTable.build()

        val routingEngine = RoutingEngine(routingTable);
        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two"), TestHttpServletResponse())

        Assert.assertTrue(ObjectRoutes.methodsCalled.contains("beforeExceptOne"))
        Assert.assertTrue(ObjectRoutes.methodsCalled.contains("get"))
        Assert.assertTrue(ObjectRoutes.methodsCalled.contains("afterExceptOne"))

        ObjectRoutes.methodsCalled.clear()

        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two/one"), TestHttpServletResponse())

        Assert.assertFalse(ObjectRoutes.methodsCalled.contains("beforeExceptOne"))
        Assert.assertTrue(ObjectRoutes.methodsCalled.contains("one"))
        Assert.assertFalse(ObjectRoutes.methodsCalled.contains("afterExceptOne"))
    }

    @Test
    fun testObjectNoPublic() {
        val routesConfiguration = RoutesConfiguration()
        routesConfiguration.routeUnannotatedPublicMethods = false

        val routingTable = RoutingTable(routesConfiguration)

        routingTable.add(ObjectRoutes)
        routingTable.build()

        val routingEngine = RoutingEngine(routingTable);
        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two"), TestHttpServletResponse())

        Assert.assertTrue(ObjectRoutes.methodsCalled.isEmpty())
    }

    @Test
    fun testClass() {
        val routesConfiguration = RoutesConfiguration()
        routesConfiguration.routeUnannotatedPublicMethods = true

        val routingTable = RoutingTable(routesConfiguration)

        routingTable.add(ClassRoutes::class.java)
        routingTable.build()

        val routingEngine = RoutingEngine(routingTable);
        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two"), TestHttpServletResponse())

        Assert.assertTrue(ClassRoutes.methodsCalled.contains("beforeExceptOne"))
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("get"))
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("afterExceptOne"))

        ClassRoutes.methodsCalled.clear()

        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two/one"), TestHttpServletResponse())

        Assert.assertFalse(ClassRoutes.methodsCalled.contains("beforeExceptOne"))
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("one"))
        Assert.assertFalse(ClassRoutes.methodsCalled.contains("afterExceptOne"))

        routingEngine.process(TestHttpServletRequest("GET", "/", "/one/two/static"), TestHttpServletResponse())
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("static"))
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("beforeExceptOne"))
        Assert.assertTrue(ClassRoutes.methodsCalled.contains("afterExceptOne"))

    }
}