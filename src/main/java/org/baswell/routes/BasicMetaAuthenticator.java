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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A MetaAuthenticator that uses HTTP basic authentication. Once a user has successfully logged in ({@link #validCredentials(String, String)}
 * returns true) a session attribute is set and this user is allowed through for the lifetime of the session.
 */
abstract public class BasicMetaAuthenticator implements MetaAuthenticator
{
  abstract protected boolean validCredentials(String userName, String password);

  @Override
  public boolean metaRequestAuthenticated(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException
  {
    Boolean loggedIn = (Boolean) servletRequest.getSession().getAttribute(LOGGED_IN_ATTRIBUTE);
    if ((loggedIn != null) && loggedIn)
    {
      return true;
    }
    else
    {
      String authorizationHeader = servletRequest.getHeader("Authorization");
      if ((authorizationHeader == null) || !authorizationHeader.toUpperCase().startsWith("BASIC "))
      {
        requireLogin(servletResponse);
        return false;
      }
      else
      {
        String userPasswordDecoded = new String(Base64.decode(authorizationHeader.substring(6)));
        String userName, password;
        int index = userPasswordDecoded.indexOf(':');
        if (index == -1)
        {
          userName = userPasswordDecoded;
          password = null;
        }
        else
        {
          userName = userPasswordDecoded.substring(0, index);
          password = userPasswordDecoded.substring(index + 1, userPasswordDecoded.length());
        }

        if (validCredentials(userName, password))
        {
          servletRequest.getSession().setAttribute(LOGGED_IN_ATTRIBUTE, true);
          return true;
        }
        else
        {
          requireLogin(servletResponse);
          return false;
        }
      }
    }
  }

  void requireLogin(HttpServletResponse response) throws IOException
  {
    response.setHeader("WWW-Authenticate", "BASIC realm=\"Routes\"");
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }

  static private final String LOGGED_IN_ATTRIBUTE = BasicMetaAuthenticator.class.getSimpleName() + ":LoggedIn";
}
