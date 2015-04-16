package org.baswell.routes;

class Pair<X, Y>
{
  static <A, B> Pair<A, B> pair(A a, B b)
  {
    return new Pair<A, B>(a, b);
  }
  
  X x;
  
  Y y;
  
  Pair()
  {}

  Pair(X x, Y y)
  {
    this.x = x;
    this.y = y;
  }
}
