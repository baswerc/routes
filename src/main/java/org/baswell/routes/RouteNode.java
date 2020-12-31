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
import java.lang.reflect.Type;
import java.util.List;

class RouteNode implements Comparable<RouteNode>
{
  final int index;

  final Method method;
  
  final RouteConfiguration routeConfiguration;

  final RouteHolder routeHolder;
  
  final RouteCriteria criteria;
  
  final List<MethodParameter> parameters;
  
  final ResponseType responseType;

  final List<BeforeRouteNode> beforeRouteNodes;

  final List<AfterRouteNode> afterRouteNodes;

  RouteNode(int index, Method method, RouteConfiguration routeConfiguration, RouteHolder routeHolder, RouteCriteria criteria, List<MethodParameter> parameters, ResponseType responseType, List<BeforeRouteNode> beforeRouteNodes, final List<AfterRouteNode> afterRouteNodes)
  {
    this.index = index;
    this.method = method;
    this.routeConfiguration = routeConfiguration;
    this.routeHolder = routeHolder;
    this.criteria = criteria;
    this.parameters = parameters;
    this.responseType = responseType;
    this.beforeRouteNodes = beforeRouteNodes;
    this.afterRouteNodes = afterRouteNodes;
  }

  Type getRequestContentType()
  {
    for (MethodParameter parameter : parameters)
    {
      if (parameter.type == MethodRouteParameterType.REQUEST_CONTENT)
      {
        return parameter.requestContentType;
      }
    }

    for (BeforeRouteNode beforeRouteNode : beforeRouteNodes)
    {
      for (MethodParameter parameter : beforeRouteNode.parameters)
      {
        if (parameter.type == MethodRouteParameterType.REQUEST_CONTENT)
        {
          return parameter.requestContentType;
        }
      }
    }

    for (AfterRouteNode afterRouteNode : afterRouteNodes)
    {
      for (MethodParameter parameter : afterRouteNode.parameters)
      {
        if (parameter.type == MethodRouteParameterType.REQUEST_CONTENT)
        {
          return parameter.requestContentType;
        }
      }
    }

    return null;
  }

  @Override
  public int compareTo(RouteNode routeNode)
  {
    int criteriaCompare = criteria.compareTo(routeNode.criteria);
    if (criteriaCompare == 0)
    {
      return index - routeNode.index;
    }
    else
    {
      return criteriaCompare;
    }
  }
}