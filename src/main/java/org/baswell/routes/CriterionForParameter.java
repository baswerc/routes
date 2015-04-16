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
