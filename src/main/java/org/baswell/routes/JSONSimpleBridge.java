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

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;

class JSONSimpleBridge
{
  static <RequestContentType extends Object> RequestContentType parseJson(byte[] contentBytes) throws IOException
  {
    try
    {
      return (RequestContentType) new JSONParser().parse(new String(contentBytes));
    }
    catch (ParseException e)
    {
      throw new IOException(e);
    }
  }
}
