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
package org.baswell.routes.testroutes;

import org.baswell.routes.MediaType;
import org.baswell.routes.Route;
import org.baswell.routes.Routes;

@Routes(defaultReturnedStringIsContent = true)
public class ReturnTypes extends BaseRoutes
{
  @Route("/helloworld")
  public String getHelloWorld()
  {
    methodsCalled.add("getHelloWorld");
    return "Hello World";
  }

  @Route(value = "/gson", respondsToMediaRequests = MediaType.JSON)
  public GsonResponse gson()
  {
    methodsCalled.add("gson");
    GsonResponse gsonResponse = new GsonResponse();
    gsonResponse.one = "one";
    gsonResponse.two = 2;
    gsonResponse.three = true;
    return gsonResponse;
  }

  static public class GsonResponse
  {
    public String one;

    public int two;

    public boolean three;
  }
}
