package org.baswell.routes.parsing;

abstract public class ParameterTerminal extends RouteTerminal
{
  public final String name;
  
  public final boolean optional;
  
  public ParameterTerminal(int routeIndex, String name, boolean optional)
  {
    super(routeIndex);
    
    this.name = name;
    this.optional = optional;
  }
}
