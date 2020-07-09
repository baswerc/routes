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
package org.baswell.routes.utils.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TestRequestDispatcher implements RequestDispatcher
{
  public final String path;
  
  public boolean forwardCalled;
  
  public boolean includeCalled;
  
  public ServletException servletExceptionToThrow;
  
  public IOException ioExceptionToThrow;
  
  public TestRequestDispatcher(String path)
  {
    this.path = path;
  }

  @Override
  public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException
  {
    forwardCalled = true;
    if (servletExceptionToThrow != null)
    {
      throw servletExceptionToThrow;
    }
    else if (ioExceptionToThrow != null)
    {
      throw ioExceptionToThrow;
    }
  }

  @Override
  public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException
  {
    includeCalled = true;
    if (servletExceptionToThrow != null)
    {
      throw servletExceptionToThrow;
    }
    else if (ioExceptionToThrow != null)
    {
      throw ioExceptionToThrow;
    }
  }
}
