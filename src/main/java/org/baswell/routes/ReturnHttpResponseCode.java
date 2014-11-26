package org.baswell.routes;

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
