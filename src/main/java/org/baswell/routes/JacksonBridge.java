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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonBridge
{
  static <RequestContentType extends Object> RequestContentType parseJackson(byte[] contentBytes, Type contentType) throws IOException
  {
    return (RequestContentType) new ObjectMapper().readValue(contentBytes, TypeFactory.defaultInstance().constructType(contentType));
  }

  static void sendJackson(Object response, HttpServletResponse servletResponse) throws IOException
  {
    new ObjectMapper().writeValue(servletResponse.getWriter(), response);
  }
}
