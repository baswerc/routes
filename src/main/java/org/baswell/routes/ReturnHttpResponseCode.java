package org.baswell.routes;

/**
 * If thrown from {@link org.baswell.routes.BeforeRoute} or {@link org.baswell.routes.Route} methods the current request
 * will immediately (no further processing) be returned the given {@link #code}. This exception should not be thrown
 * from {@link org.baswell.routes.AfterRoute} methods since the HTTP response has already been processed.
 */
public class ReturnHttpResponseCode extends RuntimeException
{
  public static ReturnHttpResponseCode OK = new ReturnHttpResponseCode(200);

  public static ReturnHttpResponseCode BAD_REQUEST = new ReturnHttpResponseCode(400);

  public static ReturnHttpResponseCode UNAUTHORIZED = new ReturnHttpResponseCode(401);

  public static ReturnHttpResponseCode NOT_FOUND = new ReturnHttpResponseCode(404);

  public static ReturnHttpResponseCode INTERNAL_SERVER_ERROR = new ReturnHttpResponseCode(500);

  public final int code;
  
  public ReturnHttpResponseCode(int code)
  {
    this.code = code;
  }
}
