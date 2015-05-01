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

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * Convention for mapping routes where annotations aren't explicitly provided.
 *
 * @see org.baswell.routes.RoutesConfiguration#routeByConvention
 */
public interface RouteByConvention
{
  /**
   * The generated root path of a routes class when {@link org.baswell.routes.Routes#value()} is not specified. This root
   * path will only be prepended to route method paths without {@link org.baswell.routes.Route#value()}.
   *
   * @param routesClass The routes class.
   * @return The root route path.
   */
  String routesPathPrefix(Class routesClass);

  /**
   * The generated route path from a route method with no {@link org.baswell.routes.Route#value()}. The path returned
   * here will be appended to {@link #routesPathPrefix(Class)}.
   *
   * @param routeMethod
   * @return The route path.
   */
  String routePath(Method routeMethod);

  /**
   * The HTTP methods this route method responds to when {@link org.baswell.routes.Route#respondsToMethods()} is not specified.
   * @param routeMethod
   * @return The HTTP methods this route method responds to.
   */
  List<HttpMethod> respondsToMethods(Method routeMethod);
}
