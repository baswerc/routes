package org.baswell.routes.parsing;

public class PatternPathTerminal extends PathTerminal
{
  public final String pattern;
  
  public PatternPathTerminal(int routeIndex, int pathIndex, String pattern)
  {
    super(routeIndex, pathIndex, true);
    this.pattern = pattern;
  }

}
