package org.baswell.routes.kotlin

import org.baswell.routes.AfterRoute
import org.baswell.routes.BeforeRoute
import org.baswell.routes.Route
import org.baswell.routes.Routes

@Routes("two")
object ObjectRoutes : BaseRoutes() {

    val methodsCalled = mutableListOf<String>()

    @BeforeRoute(exceptTags = ["one"])
    fun beforeExceptOne() {
        methodsCalled.add("beforeExceptOne")
    }

    @AfterRoute(exceptTags = ["one"])
    fun afterExceptOne() {
        methodsCalled.add("afterExceptOne")
    }

    fun get() {
        methodsCalled.add("get")
    }

    @Route("one", tags = ["one"])
    fun getOneMethod() {
        methodsCalled.add("one")
    }
}