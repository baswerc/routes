package org.baswell.routes.parsing;

public class PatternParameterTerminal extends ParameterTerminal
{
  public final String pattern;
  
  public PatternParameterTerminal(int routeIndex, String name, String pattern, boolean optional)
  {
    super(routeIndex, name, optional, true);
    
    this.pattern = pattern;
  }
}
