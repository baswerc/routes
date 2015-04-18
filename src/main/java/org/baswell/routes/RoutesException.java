package org.baswell.routes;

/**
 * General exception thrown when Routes are configured incorrectly.
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
