package org.baswell.routes;

class ParsedExactPathTerminal extends ParsedPathTerminal
{
  final String segment;
  
  ParsedExactPathTerminal(int routeIndex, int pathIndex, String segment)
  {
    super(routeIndex, pathIndex);
    this.segment = segment;
  }
}
