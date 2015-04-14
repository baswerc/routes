package org.baswell.routes.criteria;

import java.util.regex.Pattern;

public class RequestPathSegmentCriterion
{
  public static enum RequestPathSegmentCrierionType
  {
    FIXED,
    PATTERN,
    MULTI;
  }
  
  public final int index;

  public final String value;

  public final RequestPathSegmentCrierionType type;

  public final Pattern pattern;

  public final int numberPatternGroups;
  
  RequestPathSegmentCriterion(int index, String value, RequestPathSegmentCrierionType type, Pattern pattern)
  {
    this.index = index;
    this.value = value;
    this.type = type;
    this.pattern = pattern;
    numberPatternGroups = pattern == null ? 0 : pattern.matcher("").groupCount();
  }
}
