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

class CriterionForPathSegment
{
  enum RequestPathSegmentCrierionType
  {
    FIXED,
    PATTERN,
    MULTI;
  }
  
  final int index;

  final String value;

  final RequestPathSegmentCrierionType type;

  final Pattern pattern;

  final int numberPatternGroups;
  
  CriterionForPathSegment(int index, String value, RequestPathSegmentCrierionType type, Pattern pattern)
  {
    this(index, value, type, pattern, pattern == null ? 0 : pattern.matcher("").groupCount());
  }

  CriterionForPathSegment(int index, String value, RequestPathSegmentCrierionType type, Pattern pattern, int numberPatternGroups)
  {
    this.index = index;
    this.value = value;
    this.type = type;
    this.pattern = pattern;
    this.numberPatternGroups = numberPatternGroups;
  }

}
