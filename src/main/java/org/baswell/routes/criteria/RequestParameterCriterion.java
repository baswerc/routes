package org.baswell.routes.criteria;

import java.util.regex.Pattern;

class RequestParameterCriterion
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
  
  RequestParameterCriterion(String name, String value, RequestParameterType type, boolean presenceRequired, Pattern pattern)
  {
    this.name = name;
    this.value = value;
    this.type = type;
    this.presenceRequired = presenceRequired;
    this.pattern = pattern;
  }
}
