package org.baswell.routes.parsing;

public class SymbolParameterTerminal extends ParameterTerminal
{
  public final String symbol;
  
  public SymbolParameterTerminal(int routeIndex, String name, String symbol, boolean optional)
  {
    super(routeIndex, name, optional, true);
    
    this.symbol = symbol;
  }
}
