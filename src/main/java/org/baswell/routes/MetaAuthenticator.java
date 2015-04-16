package org.baswell.routes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication scheme for Routes meta page.
 */
public interface MetaAuthenticator
{
  /**
   * Called when the current HTTP request matches {@link org.baswell.routes.RoutesConfiguration#routesMetaPath}.
   *
   * @param request
   * @param response
   * @return True if the Routes meta page is allowed to be shown, false otherwise.
   * @throws IOException
   */
  boolean metaRequestAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
