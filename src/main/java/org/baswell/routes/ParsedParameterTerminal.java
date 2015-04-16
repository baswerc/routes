package org.baswell.routes;

abstract class ParsedParameterTerminal extends ParsedRouteTerminal
{
   final String name;
  
  final boolean optional;
  
  ParsedParameterTerminal(int routeIndex, String name, boolean optional)
  {
    super(routeIndex);
    
    this.name = name;
    this.optional = optional;
  }
}
