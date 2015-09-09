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
import java.util.Set;

class AfterRouteNode implements Comparable<AfterRouteNode>
{
  final Method method;

  final List<MethodParameter> parameters;

  final Set<String> onlyTags;
  
  final Set<String> exceptTags;

  final boolean onlyOnSuccess;

  final boolean onlyOnError;
  
  final Integer explicitOrder;

  final int classHierarchyOrder;

  AfterRouteNode(Method method, List<MethodParameter> parameters, Set<String> onlyTags, Set<String> exceptTags, boolean onlyOnSuccess, boolean onlyOnError, Integer explicitOrder, int classHierarchyOrder)
  {
    this.method = method;
    this.parameters = parameters;
    this.onlyTags = onlyTags;
    this.exceptTags = exceptTags;
    this.onlyOnSuccess = onlyOnSuccess;
    this.onlyOnError = onlyOnError;
    this.explicitOrder = explicitOrder;
    this.classHierarchyOrder = classHierarchyOrder;
  }


  @Override
  public int compareTo(AfterRouteNode o)
  {
    if (explicitOrder == null && o.explicitOrder != null)
    {
      return 1;
    }
    else if (explicitOrder != null && o.explicitOrder == null)
    {
      return -1;
    }
    else if (explicitOrder != null && o.explicitOrder != null)
    {
      return explicitOrder - o.explicitOrder;
    }
    else if (classHierarchyOrder != o.classHierarchyOrder)
    {
      return o.classHierarchyOrder - classHierarchyOrder;
    }
    else
    {
      return method.getName().compareTo(o.method.getName());
    }
  }
}
