package org.baswell.routes;

public class SystemOutLogger implements RoutesLogger
{
  @Override
  public void logError(String message)
  {
    System.err.println(message);
  }

  @Override
  public void logError(Exception e)
  {
    if (e != null)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void logError(String message, Exception e)
  {
    System.err.println(message);
    if (e != null)
    {
      e.printStackTrace();
    }
  }
}
