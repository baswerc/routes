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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

class MatchedRoute
{
  final RouteNode routeNode;

  final List<Matcher> pathMatchers;

  final Map<String, Matcher> parameterMatchers;

  public MatchedRoute(RouteNode routeNode, List<Matcher> pathMatchers, Map<String, Matcher> parameterMatchers)
  {
    this.routeNode = routeNode;
    this.pathMatchers = pathMatchers;
    this.parameterMatchers = parameterMatchers;
  }
}
