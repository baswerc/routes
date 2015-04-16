package org.baswell.routes;

class ParsedExactParameterTerminal extends ParsedParameterTerminal
{
  final String value;
  
  ParsedExactParameterTerminal(int routeIndex, String name, String value, boolean optional)
  {
    super(routeIndex, name, optional);
    
    this.value = value;
  }
}
