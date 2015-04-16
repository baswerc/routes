package org.baswell.routes;

import java.util.List;

class ParsedRouteTree
{
  final List<ParsedPathTerminal> pathTerminals;
  
  final List<ParsedParameterTerminal> parameterTerminals;

  ParsedRouteTree(List<ParsedPathTerminal> pathTerminals, List<ParsedParameterTerminal> parameterTerminals)
  {
    this.pathTerminals = pathTerminals;
    this.parameterTerminals = parameterTerminals;
  }
}
