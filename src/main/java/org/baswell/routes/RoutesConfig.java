package org.baswell.routes;


public class RoutesConfig
{
  private String rootPath;
  
  private String rootForwardPath = "/WEB-INF/jsps";
  
  private boolean caseInsensitive;
  
  private String defaultContentType;
  
  private boolean defaultResponseIsBody;
  
  private int streamBufferSize = 16 * 1024;

  private RouteInstanceFactory routeInstanceFactory = new DefaultRouteInstanceFactory();
  
  private RouteFromMethodScheme routeFromMethodScheme = new SimpleRouteFromMethodScheme();

  private String routesMetaPath;
  
  public String getRootPath()
  {
    return rootPath;
  }

  public void setRootPath(String rootPath)
  {
    this.rootPath = rootPath;
  }

  public String getRootForwardPath()
  {
    return rootForwardPath;
  }

  public void setRootForwardPath(String rootForwardPath)
  {
    this.rootForwardPath = rootForwardPath;
  }

  public boolean isCaseInsensitive()
  {
    return caseInsensitive;
  }

  public void setCaseInsensitive(boolean caseInsensitive)
  {
    this.caseInsensitive = caseInsensitive;
  }

  public String getDefaultContentType()
  {
    return defaultContentType;
  }

  public void setDefaultContentType(String defaultContentType)
  {
    this.defaultContentType = defaultContentType;
  }

  public boolean isDefaultResponseIsBody()
  {
    return defaultResponseIsBody;
  }

  public void setDefaultResponseIsBody(boolean defaultResponseIsBody)
  {
    this.defaultResponseIsBody = defaultResponseIsBody;
  }

  public RouteInstanceFactory getRouteInstanceFactory()
  {
    return routeInstanceFactory;
  }

  public boolean hasRoutesMetaPath()
  {
    return (routesMetaPath != null) && !routesMetaPath.isEmpty();
  }

  public String getRoutesMetaPath()
  {
    return routesMetaPath;
  }

  public void setRoutesMetaPath(String routesMetaPath)
  {
    this.routesMetaPath = routesMetaPath == null ? null : routesMetaPath.trim();
  }

  public void setRouteInstanceFactory(RouteInstanceFactory routeInstanceFactory)
  {
    this.routeInstanceFactory = routeInstanceFactory;
  }

  public int getStreamBufferSize()
  {
    return streamBufferSize;
  }

  public void setStreamBufferSize(int streamBufferSize)
  {
    this.streamBufferSize = streamBufferSize;
  }

  public RouteFromMethodScheme getRouteFromMethodScheme()
  {
    return routeFromMethodScheme;
  }

  public void setRouteFromMethodScheme(RouteFromMethodScheme routeFromMethodScheme)
  {
    this.routeFromMethodScheme = routeFromMethodScheme;
  }

}
