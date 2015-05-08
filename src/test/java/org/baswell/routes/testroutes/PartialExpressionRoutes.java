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
package org.baswell.routes.testroutes;

import org.baswell.routes.MediaType;
import org.baswell.routes.RequestedMediaType;
import org.baswell.routes.Route;

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
  public void getReport(String reportName, String extension, RequestedMediaType requestedMediaType)
  {
    assertEquals(MediaType.PDF, requestedMediaType.mediaType);
    methodsCalled.add("getReport");
    PartialExpressionRoutes.reportName = reportName;
    PartialExpressionRoutes.extension = extension;
  }
}
