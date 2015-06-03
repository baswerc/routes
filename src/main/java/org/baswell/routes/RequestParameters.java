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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import static org.baswell.routes.RoutesMethods.*;

/**
 * The parameters from an HTTP request. Default parameters for a route ({@link org.baswell.routes.Route#defaultParameters()})
 * will be present for any parameters not explicitly set in the HTTP request.
 */
public class RequestParameters
{
  final Map<String, List<String>> parameters;

  final String queryString;

  RequestParameters(HttpServletRequest request)
  {
    this(request.getParameterMap());
  }

  RequestParameters(Map<String, String[]> parameterMap)
  {
    parameters = new HashMap<String, List<String>>();
    StringBuilder queryStringBuilder = new StringBuilder();
    int index = 0;
    for (Entry<String, String[]> entry : parameterMap.entrySet())
    {
      String key = entry.getKey();
      String[] values = entry.getValue();
      parameters.put(key, new ArrayList<String>(Arrays.asList(values)));
      for (String value : values)
      {
        if (index > 0) queryStringBuilder.append('&');
        ++index;
        queryStringBuilder.append(key).append('=').append(value);
      }
    }
    queryString = queryStringBuilder.toString();
  }

  RequestParameters(String queryString)
  {
    parameters = new HashMap<String, List<String>>();
    String[] parameterValues = queryString.split("&");
    for (String parameterValue : parameterValues)
    {
      if (!parameterValue.trim().isEmpty())
      {
        String[] parameter = parameterValue.split("=");
        if (parameter.length == 2)
        {
          String key = parameter[0].trim();
          String value = parameter[1].trim();
          if (!parameters.containsKey(key))
          {
            parameters.put(key, new ArrayList<String>());
          }
          parameters.get(key).add(value);
        }
      }
    }
    this.queryString = queryString;
  }

  /**
   *
   * @return {@link #size()} > 0
   */
  public boolean hasParameters()
  {
    return !parameters.isEmpty();
  }

  /**
   *
   * @return The number of parameters.
   */
  public int size()
  {
    return parameters.size();
  }

  /**
   *
   * @param name The name of the parameter
   * @return The number parameter values for the given parameter name.
   */
  public int size(String name)
  {
    List<String> vals = getValues(name);
    return vals == null ? 0 : vals.size();
  }

  /**
   *
   * @param name The parameter name.
   * @return True if a value for the given parameter name exists.
   */
  public boolean contains(String name)
  {
    return parameters.containsKey(name);
  }

  /**
   *
   * @param name The parameter name.
   * @return True if a value for the given parameter name exists and has content (not an empty string).
   */
  public boolean containsContent(String name)
  {
    if (parameters.containsKey(name))
    {
      List<String> values = parameters.get(name);
      if (values != null)
      {
        for (String value : values)
        {
          if (value != null && !value.trim().isEmpty())
          {
            return true;
          }
        }
      }
      return false;
    }
    else
    {
      return false;
    }
  }

  /**
   *
   * @return A parameter map where the key is the parameter name and the value is the parameter values.
   */
  public Map<String, List<String>> getParameterListMap()
  {
    return new HashMap<String, List<String>>(parameters);
  }

  /**
   * Parameters with multiple values will only contain one value in this map.
   *
   * @return A parameter map where the key is the parameter name and the value is the parameter value.
   */
  public Map<String, String> getParameterMap()
  {
    Map<String, String> parameterMap = new HashMap<String, String>();
    for (Entry<String, List<String>> entry : parameters.entrySet())
    {
      parameterMap.put(entry.getKey(), entry.getValue().get(0));
    }
    return parameterMap;
  }

  /**
   *
   * @param name The parameter name.
   * @return The parameter value or null if not present.
   */
  public String get(String name)
  {
    return get(name, null);
  }

  /**
   *
   * @param name The parameter name.
   * @param defaultValue The default value to return if the given parameter is not present.
   * @return The parameter value or defaultValue if not present.
   */
  public String get(String name, String defaultValue)
  {
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name))
      {
        if (hasContent(value))
        {
          return value;
        }
      }
      return parameters.get(name).get(0);
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return The parameter values for the given name. If no values for the given parameter exists an empty list is returned.
   */
  public List<String> getValues(String name)
  {
    List<String> values = new ArrayList<String>();
    if (parameters.containsKey(name))
    {
      values.addAll(parameters.get(name));
    }
    return values;
  }

  /**
   *
   * @param name The parameter name
   * @return The first character of the parameter value or <status>null</status> if the given parameter doesn't exist.
   */
  public Character getCharacter(String name)
  {
    return getCharacter(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The value to return if the parameter doesn't exists.
   * @return The first character of the parameter value or the given defaultValue if the given parameter doesn't exist.
   */
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

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value as a Character for the given name. If no values for the given parameter exists an empty list is returned.
   */
  public List<Character> getCharacters(String name)
  {
    List<Character> values = new ArrayList<Character>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (!values.isEmpty()) values.add(value.charAt(0));
    }
    return values;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Boolean or <status>null</status> if the given parameter doesn't exist.
   * @see java.lang.Boolean#parseBoolean(String)
   */
  public Boolean getBoolean(String name)
  {
    return getBoolean(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The value to return if the parameter doesn't exists
   * @return The parameter value parsed as a Boolean or the given defaultValue if the given parameter doesn't exist.
   * @see java.lang.Boolean#parseBoolean(String)
   */
  public Boolean getBoolean(String name, Boolean defaultValue)
  {
    if (hasContent(name))
    {
      return Boolean.parseBoolean(get(name).trim());
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Boolean for the given name. If no values for the given parameter exists an empty list is returned.
   * @see java.lang.Boolean#parseBoolean(String)
   */
  public List<Boolean> getBooleans(String name)
  {
    List<Boolean> values = new ArrayList<Boolean>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) values.add(Boolean.parseBoolean(value.trim()));
    }
    return values;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Byte or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Byte.
   * @see java.lang.Byte#parseByte(String)
   */
  public Byte getByte(String name) throws NumberFormatException
  {
    return getByte(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Byte or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Byte.
   * @see java.lang.Byte#parseByte(String)
   */
  public Byte getByte(String name, Byte defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Byte.parseByte(get(name));
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Byte for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Byte.
   * @see java.lang.Byte#parseByte(String)
   */
  public List<Byte> getBytes(String name) throws NumberFormatException
  {
    List<Byte> bytes = new ArrayList<Byte>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) bytes.add(Byte.parseByte(value.trim()));
    }
    return bytes;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Short or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Short.
   * @see java.lang.Short#parseShort(String)
   */
  public Short getShort(String name) throws NumberFormatException
  {
    return getShort(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Short or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Short.
   * @see java.lang.Short#parseShort(String)
   */
  public Short getShort(String name, Short defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Short.parseShort(get(name).trim());
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Short for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Short.
   * @see java.lang.Short#parseShort(String)
   */
  public List<Short> getShorts(String name) throws NumberFormatException
  {
    List<Short> shorts = new ArrayList<Short>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) shorts.add(Short.parseShort(value.trim()));
    }
    return shorts;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Integer or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Integer.
   * @see java.lang.Integer#parseInt(String)
   */
  public Integer getInteger(String name) throws NumberFormatException
  {
    return getInteger(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Integer or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Integer.
   * @see java.lang.Integer#parseInt(String)
   */
  public Integer getInteger(String name, Integer defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Integer.parseInt(get(name));
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Integer for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Integer.
   * @see java.lang.Integer#parseInt(String)
   */
  public List<Integer> getIntegers(String name) throws NumberFormatException
  {
    List<Integer> ints = new ArrayList<Integer>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) ints.add(Integer.parseInt(value.trim()));
    }
    return ints;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Long or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Long.
   * @see java.lang.Long#parseLong(String)
   */
  public Long getLong(String name) throws NumberFormatException
  {
    return getLong(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Long or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Long.
   * @see java.lang.Long#parseLong(String)
   */
  public Long getLong(String name, Long defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Long.parseLong(get(name).trim());
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Long for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Long.
   * @see java.lang.Boolean#parseBoolean(String)
   */
  public List<Long> getLongs(String name) throws NumberFormatException
  {
    List<Long> longs = new ArrayList<Long>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) longs.add(Long.parseLong(value.trim()));
    }
    return longs;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Float or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Float.
   * @see java.lang.Float#parseFloat(String)
   */
  public Float getFloat(String name) throws NumberFormatException
  {
    return getFloat(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Float or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Float.
   * @see java.lang.Float#parseFloat(String)
   */
  public Float getFloat(String name, Float defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Float.parseFloat(get(name).trim());
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Float for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Float.
   * @see java.lang.Long#parseLong(String)
   */
  public List<Float> getFloats(String name) throws NumberFormatException
  {
    List<Float> floats = new ArrayList<Float>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) floats.add(Float.parseFloat(value.trim()));
    }
    return floats;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Double or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Double.
   * @see java.lang.Double#parseDouble(String)
   */
  public Double getDouble(String name) throws NumberFormatException
  {
    return getDouble(name, null);
  }

  /**
   *
   * @param name The parameter name
   * @param defaultValue The default value to return if the parameter doesn't exists.
   * @return The parameter value parsed as a Double or the given defaultValue if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Double.
   * @see java.lang.Double#parseDouble(String)
   */
  public Double getDouble(String name, Double defaultValue) throws NumberFormatException
  {
    if (hasContent(name))
    {
      return Double.parseDouble(get(name).trim());
    }
    else
    {
      return defaultValue;
    }
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Double for the given name. If no values for the given parameter exists an empty list is returned.
   * @throws java.lang.NumberFormatException If one of the parameter values cannot be parsed into a Double.
   * @see java.lang.Double#parseDouble(String)
   */
  public List<Double> getDoubles(String name) throws NumberFormatException
  {
    List<Double> doubles = new ArrayList<Double>();
    if (parameters.containsKey(name))
    {
      for (String value : parameters.get(name)) if (hasContent(value)) doubles.add(Double.parseDouble(value.trim()));
    }
    return doubles;
  }

  @Override
  public String toString()
  {
    return queryString;
  }

  void set(String name, List<String> values)
  {
    parameters.put(name, values);
  }
}
