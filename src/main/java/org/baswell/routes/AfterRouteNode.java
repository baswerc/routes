package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.baswell.routes.invoking.RouteMethodParameter;

class AfterRouteNode implements Comparable<AfterRouteNode>
{
  final Method method;

  final List<RouteMethodParameter> parameters;

  final Set<String> onlyTags;
  
  final Set<String> exceptTags;
  
  final Integer order;
  
  AfterRouteNode(Method method, List<RouteMethodParameter> parameters, Set<String> onlyTags, Set<String> exceptTags, Integer order)
  {
    this.method = method;
    this.parameters = parameters;
    this.onlyTags = onlyTags;
    this.exceptTags = exceptTags;
    this.order = order;
  }


  @Override
  public int compareTo(AfterRouteNode o)
  {
    int o1 = order == null ? Integer.MAX_VALUE : order;
    int o2 = o.order == null ? Integer.MAX_VALUE : o.order;
    
    if (o1 == o2)
    {
      return method.getName().compareTo(o.method.getName());
    }
    else
    {
      return o1 - o2;
    }
  }
}
