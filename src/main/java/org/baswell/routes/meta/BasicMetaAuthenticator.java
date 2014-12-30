package org.baswell.routes.meta;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

abstract public class BasicMetaAuthenticator implements MetaAuthenticator
{
  abstract protected boolean validCredentials(String userName, String password);

  @Override
  public boolean metaRequestAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    Boolean loggedIn = (Boolean) request.getSession().getAttribute(LOGGED_IN_ATTRIBUTE);
    if ((loggedIn != null) && loggedIn)
    {
      return true;
    }
    else
    {
      String authorizationHeader = request.getHeader("Authorization");
      if ((authorizationHeader == null) || !authorizationHeader.toUpperCase().startsWith("BASIC "))
      {
        requireLogin(response);
        return false;
      }
      else
      {
        String userPasswordDecoded = new String(Base64.decodeBase64(authorizationHeader.substring(6)));
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
          request.getSession().setAttribute(LOGGED_IN_ATTRIBUTE, true);
          return true;
        }
        else
        {
          requireLogin(response);
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
