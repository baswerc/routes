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

/**
 * <p>
 * When routes are added to {@link org.baswell.routes.RoutingTable} as class objects, a route instance must be used
 * each time to process a HTTP request. This interface controls the lifecycle of those route instances.
 * </p>
 *
 * @see org.baswell.routes.RoutesConfiguration#routeInstancePool
 */
public interface RouteInstancePool
{
  /**
   * Called when a route match has been made to a method in the given class and an instance is needed to process the
   * request. If the route class is not thread safe then once an instance is returned from this method it should not
   * be used again until it's put back in the pool ({@code returnRouteInstance}).
   *
   * @param routeClass A route class that was added to the {@link org.baswell.routes.RoutingTable}.
   * @return A instance of the given route class this is ready to process an HTTP request.
   * @throws RouteInstanceBorrowException If a route instance cannot be borrowed for any reason.
   */
  Object borrowRouteInstance(Class routeClass) throws RouteInstanceBorrowException;

  /**
   * The Routes engine is done using this route instance.
   *
   * @param routeInstance A route object previous retrieved from {@code borrowRouteInstance}.
   */
  void returnRouteInstance(Object routeInstance);
}
