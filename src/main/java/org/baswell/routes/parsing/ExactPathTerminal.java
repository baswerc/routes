package org.baswell.routes.parsing;

public class ExactPathTerminal extends PathTerminal
{
  public final String segment;
  
  public ExactPathTerminal(int routeIndex, int pathIndex, String segment)
  {
    super(routeIndex, pathIndex, false);
    this.segment = segment;
  }
}
