package org.baswell.routes;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Router
{
  private String rootPath = "";

  private String forwardRoot = "/WEB-INF/jsps";

  public void setRootPath(String rootPath)
  {
    this.rootPath = rootPath;
  }

  public void setForwardRoot(String forwardRoot)
  {
    this.forwardRoot = forwardRoot;
  }

  public Router addRoutes(Object... routes)
  {
    
    return this;
  }
  
  public void setRoutes(List routes) throws InvalidRouteException
  {
    
  }
  
  boolean route(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    return false;
  }
  
  void add(Object obj)
  {
    Object routeObject;
    Class routeClass;
    
    if (obj instanceof Class)
    {
      routeObject = null;
      routeClass = (Class)obj;
    }
    else
    {
      routeObject = obj;
      routeClass = obj.getClass();
    }
    
    
  }
}
