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

import org.baswell.routes.CriterionForPathSegment.RequestPathSegmentCrierionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.baswell.routes.RoutesMethods.size;

class RoutesCriteria
{
  final List<Pattern> acceptTypePatterns;

  final List<CriterionForPathSegment> pathCriteria;

  final RoutesConfiguration routesConfiguration;

  final boolean allCriteriaFixed;

  final boolean hasPattern;

  final boolean hasMultiPathCriterion;

  RoutesCriteria(List<CriterionForPathSegment> pathCriteria, List<Pattern> acceptTypePatterns, RoutesConfiguration routesConfiguration)
  {
    this.acceptTypePatterns = acceptTypePatterns;
    this.pathCriteria = pathCriteria;
    this.routesConfiguration = routesConfiguration;

    boolean hasPattern = false;
    boolean hasMultiPathCriterion = false;
    if (pathCriteria != null)
    {
      for (CriterionForPathSegment pathCriterion : pathCriteria)
      {
        if (pathCriterion.type == RequestPathSegmentCrierionType.PATTERN)
        {
          hasPattern = true;
        }
        else if (pathCriterion.type == RequestPathSegmentCrierionType.MULTI)
        {
          hasMultiPathCriterion = true;
        }
      }
    }
    this.hasPattern = hasPattern;
    this.hasMultiPathCriterion = hasMultiPathCriterion;
    this.allCriteriaFixed = !this.hasPattern && !this.hasMultiPathCriterion;
  }

  boolean matches(RequestedMediaType requestedMediaType, RequestPath path)
  {
    if (!acceptTypePatterns.isEmpty()) {
      boolean oneMatched = false;
      for (int i = 0; i < acceptTypePatterns.size(); i++) {
        if (acceptTypePatterns.get(i).matcher(requestedMediaType.mimeType).matches()) {
          oneMatched = true;
          break;
        }
      }

      if (!oneMatched) {
        return false;
      }
    }

    if (pathCriteria.size() > path.size()) {
      return false;
    }

    CriteriaLoop:
    for (int i = 0; i < pathCriteria.size(); i++) {
      CriterionForPathSegment criterion = pathCriteria.get(i);
      String segment = path.get(i);
      switch (criterion.type) {
        case FIXED:
          if (routesConfiguration.caseInsensitive && !segment.equalsIgnoreCase(criterion.value))
          {
            return false;
          }
          else if (!routesConfiguration.caseInsensitive && !segment.equals(criterion.value))
          {
            return false;
          }
          break;

        case PATTERN:
          if (!criterion.pattern.matcher(segment).matches()) {
            return false;
          }

        case MULTI:
          break CriteriaLoop;
      }
    }

    return true;
  }
}
