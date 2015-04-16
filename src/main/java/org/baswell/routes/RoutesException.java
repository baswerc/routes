package org.baswell.routes;

/**
 * General exception thrown when Route classes are configured incorrectly.
 */
public class RoutesException extends RuntimeException
{
  public RoutesException(String message)
  {
    super(message);
  }

  public RoutesException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
