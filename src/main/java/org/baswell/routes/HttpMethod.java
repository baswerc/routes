package org.baswell.routes;

public enum HttpMethod
{
  GET,
  POST,
  PUT,
  DELETE,
  HEAD;
  
  static public HttpMethod fromServletMethod(String method)
  {
    for (HttpMethod httpMethod : values())
    {
      if (httpMethod.toString().equalsIgnoreCase(method))
      {
        return httpMethod;
      }
    }
    return GET; // TODO ?
  }
}
