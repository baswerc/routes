package org.baswell.routes;

abstract class ParsedPathTerminal extends ParsedRouteTerminal
{
  final int pathIndex;

  protected ParsedPathTerminal(int routeIndex, int pathIndex)
  {
    super(routeIndex);
    this.pathIndex = pathIndex;
  }
}
