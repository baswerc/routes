package org.baswell.routes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

public class RequestParameters
{
  final Map<String, List<String>> parameters;

  public RequestParameters(HttpServletRequest request)
  {
    this(request.getParameterMap());
  }

  public RequestParameters(Map<String, String[]> parameterMap)
  {
    parameters = new HashMap<String, List<String>>();
    for (Entry<String, String[]> entry : parameterMap.entrySet())
    {
      parameters.put(entry.getKey(), new ArrayList<String>(Arrays.asList(entry.getValue())));
    }
  }
  
  void initializeForRoute(RouteConfig routeConfig)
  {
    for (Entry<String, List<String>> entry : routeConfig.defaultParameters.entrySet())
    {
      if (!parameters.containsKey(entry.getKey()))
      {
        parameters.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
      }
    }
  }

  public Map<String, List<String>> getParameterListMap()
  {
    return new HashMap<String, List<String>>(parameters);
  }
  
  public Map<String, String> getParameterMap()
  {
    Map<String, String> parameterMap = new HashMap<String, String>();
    for (Entry<String, List<String>> entry : parameters.entrySet())
    {
      parameterMap.put(entry.getKey(), entry.getValue().get(0));
    }
    return parameterMap;
  }
  
  public boolean contains(String name)
  {
    return parameters.containsKey(name);
  }
  
  public int count(String name)
  {
    List<String> vals = getValues(name);
    return vals == null ? 0 : vals.size();
  }
  
  public String get(String name)
  {
    return get(name, null);
  }

  public String get(String name, String defaultValue)
  {
    if (parameters.containsKey(name))
    {
      return parameters.get(name).get(0);
    }
    else
    {
      return defaultValue;
    }
  }

  public List<String> getValues(String name)
  {
    List<String> values = new ArrayList<String>();
    if (parameters.containsKey(name))
    {
      values.addAll(parameters.get(name));
    }
    return values;
  }

  public Character getCharacter(String name)
  {
    return getCharacter(name, null);
  }

  public Character getCharacter(String name, Character defaultValue)
  {
    if (parameters.containsKey(name))
    {
      return parameters.get(name).get(0).charAt(0);
    }
    else
    {
      return defaultValue;
    }
  }
  
  public List<Character> getCharacters(String name)
  {
    List<Character> values = new ArrayList<Character>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name))
      {
        values.add(value.charAt(0));
      }
    }
    return values;
  }
  
  public Boolean getBoolean(String name)
  {
    return getBoolean(name, null);
  }

  public Boolean getBoolean(String name, Boolean defaultValue)
  {
    if (parameters.containsKey(name))
    {
      return Boolean.parseBoolean(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }
  
  public List<Boolean> getBooleans(String name)
  {
    List<Boolean> values = new ArrayList<Boolean>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name))
      {
        values.add(Boolean.parseBoolean(value));
      }
    }
    return values;
  }

  public Byte getByte(String name) throws NumberFormatException
  {
    return getByte(name, null);
  }

  public Byte getByte(String name, Byte defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Byte.parseByte(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Byte> getBytes(String name) throws NumberFormatException
  {
    List<Byte> bytes = new ArrayList<Byte>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) bytes.add(Byte.parseByte(value));
    }
    return bytes;
  }

  public Short getShort(String name) throws NumberFormatException
  {
    return getShort(name, null);
  }

  public Short getShort(String name, Short defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Short.parseShort(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Short> getShorts(String name) throws NumberFormatException
  {
    List<Short> shorts = new ArrayList<Short>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) shorts.add(Short.parseShort(value));
    }
    return shorts;
  }
  
  public Integer getInteger(String name) throws NumberFormatException
  {
    return getInt(name, null);
  }

  public Integer getInt(String name, Integer defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Integer.parseInt(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Integer> getIntegers(String name) throws NumberFormatException
  {
    List<Integer> ints = new ArrayList<Integer>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) ints.add(Integer.parseInt(value));
    }
    return ints;
  }

  public Long getLong(String name) throws NumberFormatException
  {
    return getLong(name, null);
  }

  public Long getLong(String name, Long defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Long.parseLong(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Long> getLongs(String name) throws NumberFormatException
  {
    List<Long> longs = new ArrayList<Long>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) longs.add(Long.parseLong(value));
    }
    return longs;
  }

  public Float getFloat(String name) throws NumberFormatException
  {
    return getFloat(name, null);
  }

  public Float getFloat(String name, Float defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Float.parseFloat(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Float> getFloats(String name) throws NumberFormatException
  {
    List<Float> floats = new ArrayList<Float>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) floats.add(Float.parseFloat(value));
    }
    return floats;
  }

  public Double getDouble(String name) throws NumberFormatException
  {
    return getDouble(name, null);
  }
  
  public Double getDouble(String name, Double defaultValue) throws NumberFormatException
  {
    if (parameters.containsKey(name))
    {
      return Double.parseDouble(parameters.get(name).get(0));
    }
    else
    {
      return defaultValue;
    }
  }

  public List<Double> getDoubles(String name) throws NumberFormatException
  {
    List<Double> doubles = new ArrayList<Double>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) doubles.add(Double.parseDouble(value));
    }
    return doubles;
  }
}
