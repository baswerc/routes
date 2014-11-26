package org.baswell.routes;

public class RedirectTo extends RuntimeException
{
  public final String redirectUrl;
  
  public RedirectTo(String redirectUrl)
  {
    assert redirectUrl != null && !redirectUrl.isEmpty();
    
    this.redirectUrl = redirectUrl;
  }
}
