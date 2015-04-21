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
 * When routes are added to the {@link org.baswell.routes.RoutingTable} as class objects, a new route instance must be
 * created each time to process a HTTP request. If you want to control how this creation occurs implement this interface
 * and set {@link org.baswell.routes.RoutesConfiguration#routeInstanceFactory}.
 *
 * @see org.baswell.routes.RoutingTable
 * @see org.baswell.routes.RoutesConfiguration
 * @see org.baswell.routes.DefaultRouteInstanceFactory
 */
public interface RouteInstanceFactory
{
  /**
   * @param routeClass A route class that was added to the {@link org.baswell.routes.RoutingTable}.
   * @return A instance of the given route class this is ready to process an HTTP request.
   * @throws RouteInstantiationException If the route instance cannot be created.
   */
  Object getInstanceOf(Class routeClass) throws RouteInstantiationException;

  /**
   * Callback when an object previously returned from {@link #getInstanceOf(Class)} is done processing the HTTP request.
   * @param object The route object.
   */
  void doneUsing(Object object);
}
