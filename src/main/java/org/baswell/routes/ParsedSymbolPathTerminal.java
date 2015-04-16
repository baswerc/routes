package org.baswell.routes;

class ParsedSymbolPathTerminal extends ParsedPathTerminal
{
  final String symbol;
  
  ParsedSymbolPathTerminal(int routeIndex, int pathIndex, String symbol)
  {
    super(routeIndex, pathIndex);
    this.symbol = symbol;
  }

}
