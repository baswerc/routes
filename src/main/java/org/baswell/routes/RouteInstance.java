package org.baswell.routes;

public class RouteInstance
{
  public final Object instance;
  
  public final Class clazz;
  
  public final RouteInstanceFactory factory;
  
  public RouteInstance(Object instance)
  {
    this.instance = instance;
    clazz = null;
    factory = null;
  }
  
  public RouteInstance(Class clazz, RouteInstanceFactory factory)
  {
    instance = null;
    this.clazz = clazz;
    this.factory = factory;
  }
  
  public Object create()
  {
    return (instance == null) ? factory.create(clazz) : instance;
  }
}
