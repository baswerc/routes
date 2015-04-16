package org.baswell.routes;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

class BeforeRouteNode implements Comparable<BeforeRouteNode>
{
  final Method method;

  final List<MethodParameter> parameters;
  
  final boolean returnsBoolean;
  
  final Set<String> onlyTags;
  
  final Set<String> exceptTags;
  
  final Integer order;

  BeforeRouteNode(Method method, List<MethodParameter> parameters, boolean returnsBoolean, Set<String> onlyTags, Set<String> exceptTags, Integer order)
  {
    this.method = method;
    this.parameters = parameters;
    this.returnsBoolean = returnsBoolean;
    this.onlyTags = onlyTags;
    this.exceptTags = exceptTags;
    this.order = order;
  }

  @Override
  public int compareTo(BeforeRouteNode o)
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
