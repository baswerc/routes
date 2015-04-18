package org.baswell.routes.testroutes;

import org.baswell.routes.MediaType;
import org.baswell.routes.RequestFormat;
import org.baswell.routes.Route;

import javax.print.attribute.standard.Media;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class PartialExpressionRoutes extends BaseRoutes
{
  static public void main(String[] args) throws Exception
  {
    Pattern pattern = Pattern.compile("(.*)\\.(.*)");
    System.out.println(pattern.flags());
    Matcher matcher = pattern.matcher("");
    System.out.println(matcher.groupCount());
  }

  public static String reportName;

  public static String extension;

  @Route("/{(.*)\\.(.*)}")
  public void getReport(String reportName, String extension, RequestFormat requestFormat)
  {
    assertEquals(MediaType.PDF, requestFormat.mediaType);
    methodsCalled.add("getReport");
    PartialExpressionRoutes.reportName = reportName;
    PartialExpressionRoutes.extension = extension;
  }
}
