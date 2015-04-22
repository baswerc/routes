package org.baswell.routes;

/**
 * Routes will only log error statements using this interface.
 *
 * @see org.baswell.routes.RoutesConfiguration#logger
 */
public interface RoutesLogger
{
  void logError(String message);

  void logError(Exception e);

  void logError(String message, Exception e);
}
