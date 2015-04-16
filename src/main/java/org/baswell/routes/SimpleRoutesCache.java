package org.baswell.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleRoutesCache implements RoutesCache
{
  private final int maxCachedRoutes;

  private final int minCleanMSecs;

  private final boolean parametersUsedInRouting;

  private volatile long lastCleanAt = System.currentTimeMillis();

  private final Map<String, RouteCachedNode> cachedNodes = new ConcurrentHashMap<String, RouteCachedNode>();

  public SimpleRoutesCache(int maxCachedRoutes, int minCleanMSecs)
  {
    this(maxCachedRoutes, minCleanMSecs, true);
  }

  public SimpleRoutesCache(int maxCachedRoutes, int minCleanMSecs, boolean parametersUsedInRouting)
  {
    assert maxCachedRoutes > 0;
    assert minCleanMSecs > 0;

    this.maxCachedRoutes = maxCachedRoutes;
    this.minCleanMSecs = minCleanMSecs;
    this.parametersUsedInRouting = false;
  }

  @Override
  public Object get(HttpMethod method, RequestFormat requestFormat, RequestPath path, RequestParameters parameters)
  {
    RouteCachedNode cachedNode = cachedNodes.get(getKey(method, requestFormat, path, parameters));
    return cachedNode == null ? null : cachedNode.accessed();
  }

  @Override
  public void put(Object routeNode, HttpMethod method, RequestFormat requestFormat, RequestPath path, RequestParameters parameters)
  {
    String key = getKey(method, requestFormat, path, parameters);
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

  protected String getKey(HttpMethod method, RequestFormat requestFormat, RequestPath path, RequestParameters parameters)
  {
    StringBuilder keyBuilder = new StringBuilder(method.toString()).append(':').append(requestFormat).append(':').append(path);
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
