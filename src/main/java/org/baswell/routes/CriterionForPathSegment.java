package org.baswell.routes;

import java.util.regex.Pattern;

class CriterionForPathSegment
{
  static enum RequestPathSegmentCrierionType
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
