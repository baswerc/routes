package org.baswell.routes;

/**
 *
 * @see org.baswell.routes.RouteInstanceFactory#getInstanceOf(Class)
 */
public class RouteInstantiationException extends RuntimeException
{
  public RouteInstantiationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
