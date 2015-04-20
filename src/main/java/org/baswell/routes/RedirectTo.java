package org.baswell.routes;

import java.net.URL;

/**
 * If thrown from {@link org.baswell.routes.BeforeRoute} or {@link org.baswell.routes.Route} methods the current request
 * will immediately (no further processing) be redirected to the given {@link #redirectUrl}. This exception should not be thrown
 * from {@link org.baswell.routes.AfterRoute} methods since the HTTP response has already been processed.
 *
 * If the given redirectUrl is not a fully qualified URL then it will be considered relative from the current HTTP request.
 */
public class RedirectTo extends RuntimeException
{
  public final String redirectUrl;
  
  public RedirectTo(String redirectUrl)
  {
    assert redirectUrl != null && !redirectUrl.isEmpty();
    
    this.redirectUrl = redirectUrl;
  }

  public RedirectTo(URL redirectUrl)
  {
    assert redirectUrl != null;

    this.redirectUrl = redirectUrl.toString();
  }
}
