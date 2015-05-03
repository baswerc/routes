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

class RouteInstance
{
  final Object instance;
  
  final Class clazz;
  
  final RouteInstancePool factory;

  final boolean createdFromFactory;
  
  RouteInstance(Object instance)
  {
    this.instance = instance;
    clazz = null;
    factory = null;
    createdFromFactory = false;
  }
  
  RouteInstance(Class clazz, RouteInstancePool factory)
  {
    instance = null;
    this.clazz = clazz;
    this.factory = factory;
    createdFromFactory = true;
  }
  
  Object create() throws RouteInstanceBorrowException
  {
    return (instance == null) ? factory.borrowRouteInstance(clazz) : instance;
  }
}
