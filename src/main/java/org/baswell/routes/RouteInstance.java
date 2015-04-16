package org.baswell.routes;

public class RouteInstance
{
  public final Object instance;
  
  public final Class clazz;
  
  public final RouteInstanceFactory factory;

  public final boolean createdFromFactory;
  
  public RouteInstance(Object instance)
  {
    this.instance = instance;
    clazz = null;
    factory = null;
    createdFromFactory = false;
  }
  
  public RouteInstance(Class clazz, RouteInstanceFactory factory)
  {
    instance = null;
    this.clazz = clazz;
    this.factory = factory;
    createdFromFactory = true;
  }
  
  public Object create()
  {
    return (instance == null) ? factory.getInstanceOf(clazz) : instance;
  }
}
