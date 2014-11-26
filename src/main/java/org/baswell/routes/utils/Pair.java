package org.baswell.routes.utils;

public class Pair<X, Y>
{
  static public <A, B> Pair<A, B> pair(A a, B b)
  {
    return new Pair<A, B>(a, b);
  }
  
  public X x;
  
  public Y y;
  
  public Pair()
  {}

  public Pair(X x, Y y)
  {
    this.x = x;
    this.y = y;
  }
}
