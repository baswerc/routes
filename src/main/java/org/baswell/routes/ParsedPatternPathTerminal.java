package org.baswell.routes;

class ParsedPatternPathTerminal extends ParsedPathTerminal
{
  final String pattern;
  
  ParsedPatternPathTerminal(int routeIndex, int pathIndex, String pattern)
  {
    super(routeIndex, pathIndex);
    this.pattern = pattern;
  }

}
