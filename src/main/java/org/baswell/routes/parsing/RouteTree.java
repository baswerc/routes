package org.baswell.routes.parsing;

import java.util.List;

public class RouteTree
{
  public final List<PathTerminal> pathTerminals;
  
  public final List<ParameterTerminal> parameterTerminals;

  public RouteTree(List<PathTerminal> pathTerminals, List<ParameterTerminal> parameterTerminals)
  {
    this.pathTerminals = pathTerminals;
    this.parameterTerminals = parameterTerminals;
  }
}
