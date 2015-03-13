package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;

import org.baswell.routes.criteria.RouteCriteria;
import org.baswell.routes.invoking.RouteMethodParameter;
import org.baswell.routes.response.RouteResponseType;

class RouteNode implements Comparable<RouteNode>
{
  final int index;

  final Method method;
  
  final RouteConfig routeConfig;

  final RouteInstance instance;
  
  final RouteCriteria criteria;
  
  final List<RouteMethodParameter> parameters;
  
  final RouteResponseType responseType;
  
  final List<BeforeRouteNode> beforeRouteNodes;

  final List<AfterRouteNode> afterRouteNodes;

  RouteNode(int index, Method method, RouteConfig routeConfig, RouteInstance instance, RouteCriteria criteria, List<RouteMethodParameter> parameters, RouteResponseType responseType, List<BeforeRouteNode> beforeRouteNodes, final List<AfterRouteNode> afterRouteNodes)
  {
    this.index = index;
    this.method = method;
    this.routeConfig = routeConfig;
    this.instance = instance;
    this.criteria = criteria;
    this.parameters = parameters;
    this.responseType = responseType;
    this.beforeRouteNodes = beforeRouteNodes;
    this.afterRouteNodes = afterRouteNodes;
  }

  @Override
  public int compareTo(RouteNode routeNode)
  {
    int criteriaCompare = criteria.compareTo(routeNode.criteria);
    return criteriaCompare != 0 ? criteriaCompare : (index - routeNode.index);
  }
}