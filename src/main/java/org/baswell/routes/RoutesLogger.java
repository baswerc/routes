package org.baswell.routes;

public interface RoutesLogger
{
  void logError(String message);

  void logError(Exception e);

  void logError(String message, Exception e);
}
