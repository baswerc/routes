package org.baswell.routes;

class ParsedSymbolParameterTerminal extends ParsedParameterTerminal
{
  final String symbol;
  
  ParsedSymbolParameterTerminal(int routeIndex, String name, String symbol, boolean optional)
  {
    super(routeIndex, name, optional);
    
    this.symbol = symbol;
  }
}
