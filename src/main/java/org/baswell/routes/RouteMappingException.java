package org.baswell.routes;

/**
 * Thrown when an HTTP request matches a route method, but the input parameters to the method cannot be properly
 * mapped from the HTTP request values.
 */
public class RouteMappingException extends RuntimeException
{
  public RouteMappingException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
