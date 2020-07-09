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

import org.baswell.routes.RequestContent;
import org.baswell.routes.Routes;

@Routes(routeUnannotatedPublicMethods =true)
public class LoginRoutes extends BaseRoutes
{
  public void post()
  {
    methodsCalled.add("post");
  }

  public void get()
  {
    methodsCalled.add("get");
  }
  
  public void getForgotPassword()
  {
    methodsCalled.add("getForgotPassword");
  }

  public void postTest(RequestContent<byte[]> content)
  {
  }
}