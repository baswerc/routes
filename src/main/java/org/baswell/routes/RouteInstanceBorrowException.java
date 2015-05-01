/*
 * Copyright 2015 Corey Baswell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.baswell.routes;

/**
 *
 * Thrown when a route instance cannot be borrowed from {@link org.baswell.routes.RouteInstancePool} for any reason.
 *
 * @see RouteInstancePool#borrowRouteInstance(Class)
 */
public class RouteInstanceBorrowException extends Exception
{
  public RouteInstanceBorrowException(String message)
  {
    super(message);
  }

  public RouteInstanceBorrowException(Throwable cause)
  {
    super(cause);
  }

  public RouteInstanceBorrowException(String message, Throwable cause)
  {
    super(message, cause);
  }
}