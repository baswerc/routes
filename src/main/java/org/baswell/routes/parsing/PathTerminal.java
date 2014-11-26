package org.baswell.routes.parsing;

abstract public class PathTerminal extends RouteTerminal
{
  public final int pathIndex;
  
  protected PathTerminal(int routeIndex, int pathIndex, boolean canBeMappedToMethodParameter)
  {
    super(routeIndex, canBeMappedToMethodParameter);
    this.pathIndex = pathIndex;
  }
}
