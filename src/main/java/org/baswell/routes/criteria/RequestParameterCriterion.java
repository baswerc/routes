package org.baswell.routes.criteria;

import java.util.regex.Pattern;

public class RequestParameterCriterion
{
  public static enum RequestParameterType
  {
    FIXED,
    PATTERN;
  }
  
  public final String name;

  public final String value;

  public final RequestParameterType type;

  public final boolean presenceRequired;

  public final Pattern pattern;

  public final int numberPatternGroups;
  
  RequestParameterCriterion(String name, String value, RequestParameterType type, boolean presenceRequired, Pattern pattern)
  {
    this.name = name;
    this.value = value;
    this.type = type;
    this.presenceRequired = presenceRequired;
    this.pattern = pattern;
    numberPatternGroups = pattern == null ? 0 : pattern.matcher("").groupCount();
  }
}
