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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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

  public RequestParameters(HttpServletRequest request)
  {
    this(request.getParameterMap());
  }

  public RequestParameters(Map<String, String[]> parameterMap)
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

  public RequestParameters(String queryString)
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
   * @return The list of parameter names.
   */
  public List<String> getParameterNames()
  {
    return new ArrayList<String>(parameters.keySet());
  }

  /**
   *
   * @param name The parameter name.
   * @return The parameter value or null if not present.
   */
  public @Nullable String get(String name)
  {
    return getOptionalString(name, null);
  }

  /**
   *
   * @param name The parameter name.
   * @param defaultValue The default value to return if the given parameter is not present.
   * @return The parameter value or defaultValue if not present.
   */
  public String get(String name, @NotNull String defaultValue)
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
    }

    return defaultValue;
  }

  public @Nullable String getOptionalString(String name, @Nullable String defaultValue)
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
    }

    return defaultValue;
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
  public boolean getBoolean(String name)
  {
    return Boolean.parseBoolean(get(name).trim());
  }

  public boolean getBoolean(String name, boolean defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Boolean.parseBoolean(get(name).trim());
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public @Nullable Boolean getOptionalBoolean(String name)
  {
    return getOptionalBoolean(name, null);
  }

  public @Nullable Boolean getOptionalBoolean(String name, @Nullable Boolean defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Boolean.parseBoolean(get(name).trim());
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  /**
   *
   * @param name The parameter name.
   * @return Each parameter value parsed into a Boolean for the given name. If no values for the given parameter exists an empty list is returned.
   * @see java.lang.Boolean#parseBoolean(String)
   */
  public List<Boolean> getBooleans(String name)
  {
    List<String> values = getValues(name);
    List<Boolean> booleanValues = new ArrayList();
    for (String value : values)
    {
      try
      {
        booleanValues.add(Boolean.parseBoolean(value));
      }
      catch (Exception e)
      {}
    }

    return booleanValues;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Byte or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Byte.
   * @see java.lang.Byte#parseByte(String)
   */
  public byte getByte(String name) throws NumberFormatException
  {
    return Byte.parseByte(getNumber(name));
  }

  public byte getByte(String name, byte defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Byte.parseByte(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }


  public @Nullable Byte getOptionalByte(String name)
  {
    return getOptionalByte(name, null);
  }

  public @Nullable Byte getOptionalByte(String name, @Nullable Byte defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Byte.parseByte(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
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
    List<String> values = getNumbers(name);
    List<Byte> byteValues = new ArrayList();
    for (String value : values)
    {
      try
      {
        byteValues.add(Byte.parseByte(value));
      }
      catch (Exception e)
      {}
    }

    return byteValues;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Short or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Short.
   * @see java.lang.Short#parseShort(String)
   */
  public short getShort(String name) throws NumberFormatException
  {
    return Short.parseShort(getNumber(name));
  }

  public short getShort(String name, short defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Short.parseShort(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }


  public @Nullable Short getOptionalShort(String name)
  {
    return getOptionalShort(name, null);
  }

  public @Nullable Short getOptionalShort(String name, @Nullable Short defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Short.parseShort(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
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
    List<String> values = getNumbers(name);
    List<Short> shortValues = new ArrayList();
    for (String value : values)
    {
      try
      {
        shortValues.add(Short.parseShort(value));
      }
      catch (Exception e)
      {}
    }

    return shortValues;
  }

  /**
   *
   * @param name The parameter name
   * @return The parameter value parsed as a Integer or <status>null</status> if the given parameter doesn't exist.
   * @throws java.lang.NumberFormatException If the parameter value cannot be parsed into a Integer.
   * @see java.lang.Integer#parseInt(String)
   */
  public int getInteger(String name) throws NumberFormatException
  {
    return Integer.parseInt(getNumber(name));
  }

  public int getInteger(String name, int defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Integer.parseInt(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public @Nullable Integer getOptionalInteger(String name)
  {
    return getOptionalInteger(name, null);
  }

  public @Nullable Integer getOptionalInteger(String name, @Nullable Integer defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Integer.parseInt(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public List<Integer> getIntegers(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Integer> integers = new ArrayList();
    for (String value : values)
    {
      try
      {
        integers.add(Integer.parseInt(value));
      }
      catch (Exception e)
      {}
    }

    return integers;
  }

  public long getLong(String name) throws NumberFormatException
  {
    return Long.parseLong(getNumber(name));
  }

  public long getLong(String name, long defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Long.parseLong(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public @Nullable Long getOptionalLong(String name)
  {
    return getOptionalLong(name, null);
  }

  public @Nullable Long getOptionalLong(String name, @Nullable Long defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Long.parseLong(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public List<Long> getLongs(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Long> longs = new ArrayList();
    for (String value : values)
    {
      try
      {
        longs.add(Long.parseLong(value));
      }
      catch (Exception e)
      {}
    }

    return longs;
  }

  public List<Long> getRequiredLongs(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Long> longs = new ArrayList();
    for (String value : values)
    {
      longs.add(Long.parseLong(value));
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
  public float getFloat(String name) throws NumberFormatException
  {
    return Float.parseFloat(getNumber(name));
  }

  public float getFloat(String name, float defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Float.parseFloat(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public @Nullable Float getOptionalFloat(String name)
  {
    return getOptionalFloat(name, null);
  }

  public @Nullable Float getOptionalFloat(String name, @Nullable Float defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Float.parseFloat(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public List<Float> getFloats(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Float> floats = new ArrayList();
    for (String value : values)
    {
      try
      {
        floats.add(Float.parseFloat(value));
      }
      catch (Exception e)
      {}
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
  public double getDouble(String name) throws NumberFormatException
  {
    return Double.parseDouble(getNumber(name));
  }

  public double getDouble(String name, double defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Double.parseDouble(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public @Nullable Double getOptionalDouble(String name)
  {
    return getOptionalDouble(name, null);
  }

  public @Nullable Double getOptionalDouble(String name, @Nullable Double defaultValue)
  {
    if (containsContent(name))
    {
      try
      {
        return Double.parseDouble(getNumber(name));
      }
      catch (Exception e)
      {}
    }

    return defaultValue;
  }

  public List<Double> getDoubles(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Double> doubles = new ArrayList();
    for (String value : values)
    {
      try
      {
        doubles.add(Double.parseDouble(value));
      }
      catch (Exception e)
      {}
    }

    return doubles;
  }

  public List<Double> getRequiredDoubles(String name) throws NumberFormatException
  {
    List<String> values = getNumbers(name);
    List<Double> doubles = new ArrayList();
    for (String value : values)
    {
      doubles.add(Double.parseDouble(value));
    }

    return doubles;
  }


  /**
   * Removes the given parameter.
   *
   * @param name The parameter name to remove.
   */
  public void remove(String name)
  {
    parameters.remove(name);
  }

  /**
   * Adds the given parameter. If value is an instanceof java.util.Collection the parameter will be added as multi-value.
   *
   * @param name The parameter name.
   * @param value The parameter value.
   */
  public void put(String name, Object value)
  {
    List<String> values = new ArrayList<String>();
    if (value instanceof Collection)
    {
      for (Object collectionValue : (Collection) value)
      {
        values.add(collectionValue.toString());
      }
    }
    else
    {
      values.add(value.toString());
    }

    if (parameters.containsKey(name))
    {
      parameters.get(name).addAll(values);
    }
    else
    {
      parameters.put(name, values);
    }
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

  private String getNumber(String name) { return get(name).replace(",", "").trim(); }

  public List<String> getNumbers(String name)
  {
    List<String> numbers = new ArrayList();
    List<String> values = getValues(name);
    for (String value : values)
    {
      numbers.add(value.replace(",", "").trim());
    }

    return numbers;
  }

}
