package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContext
{
  public final HttpServletRequest request;
  
  public final HttpServletResponse response;
  
  public final HttpMethod method;
  
  public final RequestPath path;
  
  public final RequestParameters parameters;
  
  public final RequestFormat requestFormat;

  public RequestContext(HttpServletRequest request, HttpServletResponse response, HttpMethod method, RequestPath path, RequestParameters parameters, RequestFormat requestFormat)
  {
    this.request = request;
    this.response = response;
    this.method = method;
    this.path = path;
    this.parameters = parameters;
    this.requestFormat = requestFormat;
  }
  
  public void render(String page)
  {
    
  }
}
