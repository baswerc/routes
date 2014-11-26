package org.baswell.routes.criteria;

import java.util.regex.Pattern;

class RequestPathSegmentCriterion
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
  
  RequestPathSegmentCriterion(int index, String value, RequestPathSegmentCrierionType type, Pattern pattern)
  {
    this.index = index;
    this.value = value;
    this.type = type;
    this.pattern = pattern;
  }
}
