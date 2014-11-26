package org.baswell.routes;


public class DefaultRouteInstanceFactory implements RouteInstanceFactory
{
  @Override
  public Object create(Class clazz)
  {
    try
    {
      return clazz.getConstructor().newInstance();
    }
    catch (Exception e)
    {
      throw new RouteInstantiationException("Unable to instantiate route instance: " + clazz, e);
    }
  }
}
