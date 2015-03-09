package org.baswell.routes;


import org.baswell.routes.meta.MetaAuthenticator;

public class RoutesConfig
{
  public String rootPath;

  public String rootForwardPath = "/WEB-INF/jsps";

  public boolean caseInsensitive;

  public String defaultContentType;

  public boolean defaultResponseIsBody;

  public int streamBufferSize = 16 * 1024;

  public RouteInstanceFactory routeInstanceFactory = new DefaultRouteInstanceFactory();

  public RouteFromMethodScheme routeFromMethodScheme = new SimpleRouteFromMethodScheme();

  public String routesMetaPath;

  public MetaAuthenticator metaAuthenticator;

  public boolean hasRoutesMetaPath()
  {
    return (routesMetaPath != null) && !routesMetaPath.isEmpty();
  }
}
