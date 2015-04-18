package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;

class RouteNode implements Comparable<RouteNode>
{
  final int index;

  final Method method;
  
  final RouteConfiguration routeConfiguration;

  final RouteInstance instance;
  
  final Criteria criteria;
  
  final List<MethodParameter> parameters;
  
  final ResponseType responseType;

  final ResponseStringWriteStrategy responseStringWriteStrategy;
  
  final List<BeforeRouteNode> beforeRouteNodes;

  final List<AfterRouteNode> afterRouteNodes;

  RouteNode(int index, Method method, RouteConfiguration routeConfiguration, RouteInstance instance, Criteria criteria, List<MethodParameter> parameters, ResponseType responseType, ResponseStringWriteStrategy responseStringWriteStrategy, List<BeforeRouteNode> beforeRouteNodes, final List<AfterRouteNode> afterRouteNodes)
  {
    this.index = index;
    this.method = method;
    this.routeConfiguration = routeConfiguration;
    this.instance = instance;
    this.criteria = criteria;
    this.parameters = parameters;
    this.responseType = responseType;
    this.responseStringWriteStrategy = responseStringWriteStrategy;
    this.beforeRouteNodes = beforeRouteNodes;
    this.afterRouteNodes = afterRouteNodes;
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