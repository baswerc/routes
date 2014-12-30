package org.baswell.routes.meta;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MetaAuthenticator
{
  boolean metaRequestAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
