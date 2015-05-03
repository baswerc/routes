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
package org.baswell.routes.utils.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class TestHttpSession implements HttpSession
{
  public Map<String, Object> attributes = new HashMap<String, Object>();

  public Map<String, Object> values = new HashMap<String, Object>();

  private long creationTime = System.currentTimeMillis();
  
  @Override
  public long getCreationTime()
  {
    return creationTime;
  }

  @Override
  public String getId()
  {
    return "ID";
  }

  @Override
  public long getLastAccessedTime()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public ServletContext getServletContext()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void setMaxInactiveInterval(int interval)
  {}

  @Override
  public int getMaxInactiveInterval()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public HttpSessionContext getSessionContext()
  {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public Object getAttribute(String name)
  {
    return attributes.get(name);
  }

  @Override
  public Object getValue(String name)
  {
    return values.get(name);
  }

  @Override
  public Enumeration<String> getAttributeNames()
  {
    return Collections.enumeration(attributes.keySet());
  }

  @Override
  public String[] getValueNames()
  {
    List<String> names = new ArrayList<String>();
    for (String name : values.keySet()) names.add(name);
    return names.toArray(new String[names.size()]);
  }

  @Override
  public void setAttribute(String name, Object value)
  {
    attributes.put(name, value);
  }

  @Override
  public void putValue(String name, Object value)
  {
    values.put(name, value);
  }

  @Override
  public void removeAttribute(String name)
  {
    attributes.remove(name);
  }

  @Override
  public void removeValue(String name)
  {
    values.remove(name);
  }

  @Override
  public void invalidate()
  {}

  @Override
  public boolean isNew()
  {
    return true;
  }

}
