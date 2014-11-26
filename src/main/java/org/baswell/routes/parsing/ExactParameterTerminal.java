package org.baswell.routes.parsing;

public class ExactParameterTerminal extends ParameterTerminal
{
  public final String value;
  
  public ExactParameterTerminal(int routeIndex, String name, String value, boolean optional)
  {
    super(routeIndex, name, optional, false);
    
    this.value = value;
  }
}
