package org.baswell.routes;

class ParsedPatternParameterTerminal extends ParsedParameterTerminal
{
  final String pattern;
  
  ParsedPatternParameterTerminal(int routeIndex, String name, String pattern, boolean optional)
  {
    super(routeIndex, name, optional);
    
    this.pattern = pattern;
  }
}
