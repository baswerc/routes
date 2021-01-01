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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple in-memory route cache with an optional maximum size.
 */
public class InMemoryRouteCache implements RouteCache {
    public final Integer maxMemorySize;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    /**
     *
     * @param maxMemorySize The maximum amount of route nodes to hold. If <code>null</code> no maximum will be enforced. If provided the cache will stop storing
     *                      route nodes once the maximum is reached (no purge operation takes place).
     */
    public InMemoryRouteCache(Integer maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }

    @Override
    public void put(String key, Object routeNode) {
        if (maxMemorySize != null || cache.size() < maxMemorySize) {
            cache.put(key, routeNode);
        }
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }
}
