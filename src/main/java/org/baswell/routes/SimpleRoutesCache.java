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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * A very simple in memory cache implementation. This class has not been tested (well) so there are likely issues.
 * </p>
 *
 * @see org.baswell.routes.RoutesConfiguration#routesCache
 */
public class SimpleRoutesCache implements RoutesCache
{
  private final int maxCachedRoutes;

  private final int minCleanMSecs;

  private final boolean parametersUsedInRouting;

  private volatile long lastCleanAt = System.currentTimeMillis();

  private final Map<String, RouteCachedNode> cachedNodes = new ConcurrentHashMap<String, RouteCachedNode>();

  /**
   *
   * @param maxCachedRoutes The maximum number of routes to cache in memory.
   * @param minCleanMSecs The minimum number of seconds between cache cleans.
   * @param parametersUsedInRouting Are parameters used in determing route matches?
   */
  public SimpleRoutesCache(int maxCachedRoutes, int minCleanMSecs, boolean parametersUsedInRouting)
  {
    assert maxCachedRoutes > 0;
    assert minCleanMSecs > 0;

    this.maxCachedRoutes = maxCachedRoutes;
    this.minCleanMSecs = minCleanMSecs;
    this.parametersUsedInRouting = parametersUsedInRouting;
  }

  @Override
  public Object get(HttpMethod method, RequestedMediaType requestedMediaType, RequestPath path, RequestParameters parameters)
  {
    RouteCachedNode cachedNode = cachedNodes.get(getKey(method, requestedMediaType, path, parameters));
    return cachedNode == null ? null : cachedNode.accessed();
  }

  @Override
  public void put(Object routeNode, HttpMethod method, RequestedMediaType requestedMediaType, RequestPath path, RequestParameters parameters)
  {
    String key = getKey(method, requestedMediaType, path, parameters);
    cachedNodes.put(key, new RouteCachedNode(key, routeNode));
    if ((System.currentTimeMillis() - lastCleanAt) >= minCleanMSecs)
    {
      purgeIfNecessary();
    }
  }

  private synchronized void purgeIfNecessary()
  {
    if ((System.currentTimeMillis() - lastCleanAt) >= minCleanMSecs)
    {
      lastCleanAt = System.currentTimeMillis();
      if (cachedNodes.size() > maxCachedRoutes)
      {
        List<RouteCachedNode> cachedNodesList = new ArrayList<RouteCachedNode>(cachedNodes.values());
        Collections.sort(cachedNodesList);
        for (int i = (cachedNodesList.size() - 1); ((i > maxCachedRoutes) && (i >= 0)); i--)
        {
          cachedNodes.remove(cachedNodesList.get(i).key);
        }

      }
      lastCleanAt = System.currentTimeMillis();
    }
  }

  protected String getKey(HttpMethod method, RequestedMediaType requestedMediaType, RequestPath path, RequestParameters parameters)
  {
    StringBuilder keyBuilder = new StringBuilder(method.toString()).append(':').append(requestedMediaType).append(':').append(path);
    if (parametersUsedInRouting && (parameters != null) && parameters.hasParameters())
    {
      keyBuilder.append(':').append(parameters);
    }
    return keyBuilder.toString();
  }

  static class RouteCachedNode implements Comparable<RouteCachedNode>
  {
    final String key;

    final Object routeNode;

    final long createdAt = System.currentTimeMillis();

    long lastAccessedAt = createdAt;

    AtomicInteger numberAccesses = new AtomicInteger(1);

    RouteCachedNode(String key, Object routeNode)
    {
      this.key = key;
      this.routeNode = routeNode;
    }

    Object accessed()
    {
      lastAccessedAt = System.currentTimeMillis();
      numberAccesses.incrementAndGet();
      return routeNode;
    }

    @Override
    public int compareTo(RouteCachedNode cachedNode)
    {
      int numberAccessesCompare = cachedNode.numberAccesses.get() - numberAccesses.get();
      if (numberAccessesCompare != 0)
      {
        return numberAccessesCompare;
      }
      else
      {
        return (int)(cachedNode.lastAccessedAt - lastAccessedAt);
      }
    }
  }
}
