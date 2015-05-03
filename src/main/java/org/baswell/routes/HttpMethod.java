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

public enum HttpMethod
{
  GET,
  POST,
  PUT,
  DELETE,
  HEAD;

  /**
   *
   * @param servletMethod
   * @return The matched HTTP method or {@code null} if no match is found.
   * @see javax.servlet.http.HttpServletRequest#getMethod()
   */
  static public HttpMethod fromServletMethod(String servletMethod)
  {
    for (HttpMethod httpMethod : values())
    {
      if (httpMethod.toString().equalsIgnoreCase(servletMethod))
      {
        return httpMethod;
      }
    }
    return null;
  }
}
