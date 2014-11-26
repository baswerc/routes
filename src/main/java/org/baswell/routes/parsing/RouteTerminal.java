package org.baswell.routes.parsing;

abstract public class RouteTerminal
{
  public final int routeIndex;
  
  public final boolean canBeMappedToMethodParameter;
  
  public RouteTerminal(int routeIndex, boolean canBeMappedToMethodParameter)
  {
    this.routeIndex = routeIndex;
    this.canBeMappedToMethodParameter = canBeMappedToMethodParameter;
  }
}
