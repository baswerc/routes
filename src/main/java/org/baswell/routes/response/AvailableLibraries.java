package org.baswell.routes.response;

import java.util.HashMap;
import java.util.Map;

public class AvailableLibraries
{
  Map<String, Boolean> classesLoad = new HashMap<String, Boolean>();

  public boolean jsonSimpleAvailable()
  {
    return classAvailable("org.json.simple.JSONObject");
  }

  public boolean gsonAvailable()
  {
    return classAvailable("com.google.gson.Gson");
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
