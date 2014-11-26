package org.baswell.routes;

public interface RouteInstanceFactory
{
  Object create(Class clazz) throws RouteInstantiationException;
}
