package org.baswell.routes;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

class MatchedRoute
{
  final RouteNode routeNode;

  final List<Matcher> pathMatchers;

  final Map<String, Matcher> parameterMatchers;

  public MatchedRoute(RouteNode routeNode, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers)
  {
    this.routeNode = routeNode;
    this.pathMatchers = pathMatchers;
    this.parameterMatchers = parameterMatchers;
  }
}
