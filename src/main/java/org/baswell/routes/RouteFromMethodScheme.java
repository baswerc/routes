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
 * Scheme for mapping Methods without {@link org.baswell.routes.Route#value()} or {@link org.baswell.routes.Route#respondsToMethods()}
 *
 * @see DefaultRouteFromMethodScheme
 */
public interface RouteFromMethodScheme
{
  String getRootPath(Class routesClass);

  String getHttpPath(Method routeMethod);

  List<HttpMethod> getHttpMethods(Method method);
}
