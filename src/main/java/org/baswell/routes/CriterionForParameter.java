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

import java.util.regex.Pattern;

class CriterionForParameter
{
  static enum RequestParameterType
  {
    FIXED,
    PATTERN;
  }
  
  final String name;

  final String value;

  final RequestParameterType type;

  final boolean presenceRequired;

  final Pattern pattern;

  final int numberPatternGroups;
  
  CriterionForParameter(String name, String value, RequestParameterType type, boolean presenceRequired, Pattern pattern)
  {
    this(name, value, type, presenceRequired, pattern, pattern == null ? 0 : pattern.matcher("").groupCount());
  }

  CriterionForParameter(String name, String value, RequestParameterType type, boolean presenceRequired, Pattern pattern, int numberPatternGroups)
  {
    this.name = name;
    this.value = value;
    this.type = type;
    this.presenceRequired = presenceRequired;
    this.pattern = pattern;
    this.numberPatternGroups = numberPatternGroups;
  }

}
