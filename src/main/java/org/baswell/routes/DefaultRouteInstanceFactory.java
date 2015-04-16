package org.baswell.routes;


public class DefaultRouteInstanceFactory implements RouteInstanceFactory
{
  @Override
  public Object getInstanceOf(Class routeClass)
  {
    try
    {
      return routeClass.getConstructor().newInstance();
    }
    catch (Exception e)
    {
      throw new RouteInstantiationException("Unable to instantiate route instance: " + routeClass, e);
    }
  }

  @Override
  public void doneUsing(Object object)
  {}
}
