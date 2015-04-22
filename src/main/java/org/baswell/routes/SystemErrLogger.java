package org.baswell.routes;

/**
 * The default Routes logger. Logs everything to {@link java.lang.System#err}.
 *
 * @see org.baswell.routes.RoutesConfiguration#logger
 */
public class SystemErrLogger implements RoutesLogger
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
