package org.baswell.routes.parsing;

public class SymbolPathTerminal extends PathTerminal
{
  public final String symbol;
  
  public SymbolPathTerminal(int routeIndex, int pathIndex, String symbol)
  {
    super(routeIndex, pathIndex, true);
    this.symbol = symbol;
  }

}
