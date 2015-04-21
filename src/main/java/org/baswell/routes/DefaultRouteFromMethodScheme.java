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

/**
 * Default scheme used by Routes. For the the HTTP path, removes all HTTP method names from the beginning and then performs
 * lower case on remaining text. For example:
 *
 * getMyResource() -> /myresource
 *
 * getPostMyResource() -> /myresource
 *
 * getPostDeleteAnotherThing() -> /anotherthing
 *
 * For the HTTP methods, the HTTP method names are taken from the beginning of the method name. If the method name doesn't
 * start with any HTTP methods then the methods [GET, POST, PUT, DELETE] are used. For example:
 *
 * getMyResource() -> [GET]
 *
 * getPostMyResource() -> [GET, POST]
 *
 * getPostDeleteAnotherThing() -> [GET, POST, DELETE]
 *
 * doSomething() -> [GET, POST, PUT, DELETE]
 */
public class DefaultRouteFromMethodScheme extends BaseRouteFromMethodScheme
{
  @Override
  public String getRootPath(Class routesClass)
  {
    return removeRoutesControllerHandlerFromName(routesClass).toLowerCase();
  }

  @Override
  public String getHttpPath(Method routeMethod)
  {
    return removeHttpMethodsFromName(routeMethod).toLowerCase();
  }
}