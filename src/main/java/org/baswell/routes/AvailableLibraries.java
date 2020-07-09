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

import java.util.HashMap;
import java.util.Map;

class AvailableLibraries
{
  private final RoutesConfiguration configuration;

  Map<String, Boolean> classesLoad = new HashMap<String, Boolean>();

  AvailableLibraries(RoutesConfiguration configuration)
  {
    this.configuration = configuration;
  }

  boolean gsonAvailable()
  {
    return configuration.useGSONForJSON || (!configuration.useJacksonForJSON && classAvailable("com.google.gson.Gson"));
  }

  boolean jacksonAvailable()
  {
    return configuration.useJacksonForJSON || (!configuration.useGSONForJSON && classAvailable("com.fasterxml.jackson.databind.ObjectMapper"));
  }

  boolean classAvailable(String className)
  {
    if (classesLoad.containsKey(className))
    {
      return classesLoad.get(className);
    }
    else
    {
      try
      {
        Class.forName(className);
        classesLoad.put(className, true);
        return true;
      }
      catch (ClassNotFoundException e)
      {
        classesLoad.put(className, false);
        return false;
      }
    }
  }

}
