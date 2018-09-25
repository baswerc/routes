package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;

public class AddContextPathRedirectHandler implements RoutesRedirectHandler
{
  @Override
  public String getRedirectUrl(String url, HttpServletRequest request)
  {
    if (url.startsWith("/"))
    {
      String contextPath = request.getContextPath();
      if (!url.startsWith(contextPath))
      {
        url = contextPath + url;
      }
    }
    return url;
  }
}
