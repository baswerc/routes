package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;

public interface RoutesRedirectHandler
{
  String getRedirectUrl(String url, HttpServletRequest request);
}
