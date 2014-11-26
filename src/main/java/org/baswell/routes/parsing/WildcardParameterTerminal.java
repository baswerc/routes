package org.baswell.routes.parsing;

public class WildcardParameterTerminal extends ParameterTerminal
{
  public WildcardParameterTerminal(int routeIndex, String name, boolean optional)
  {
    super(routeIndex, name, optional, false);
  }
}
