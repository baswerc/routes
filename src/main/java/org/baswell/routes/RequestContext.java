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
  
  public final Format format;

  public RequestContext(HttpServletRequest request, HttpServletResponse response, HttpMethod method, RequestPath path, RequestParameters parameters, Format format)
  {
    this.request = request;
    this.response = response;
    this.method = method;
    this.path = path;
    this.parameters = parameters;
    this.format = format;
  }
  
  public void render(String page)
  {
    
  }
}
