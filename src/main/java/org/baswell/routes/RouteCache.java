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

/**
 * By default the Routes engine will sequential check each route method until a match is found. With lots of route methods, this can potentially	
 * become a performance bottle neck. If a cache implementation is provided, the Routes engine will first check the cache for a hit before running	
 * the sequential check.	
 *
 *
 * @see org.baswell.routes.RoutesConfiguration#routeCache
 */
public interface RouteCache
{
    /**
     * Called after a request has been processed by the given {@code routeNode}. This request should be cached for later retrieval
     * from {@code get} when the HTTP request input parameters match.
     *
     * @param key The cache key.
     * @param routeNode The route instance to be retrieved later from {@code get}.
     */
    void put(String key, Object routeNode);

    /**
     * Retrieve the cached route node or {@code null} on a cache miss.
     *
     * @param key The cache key.
     * @return The cached route node or {@code null} on a cache miss.
     */
    Object get(String key);
}