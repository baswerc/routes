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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication implementation for Routes meta page.
 *
 * @see org.baswell.routes.RoutesConfiguration#metaAuthenticator
 * @see org.baswell.routes.RoutesConfiguration#routesMetaPath
 */
public interface MetaAuthenticator
{
  /**
   * Called when the current HTTP request matches {@link org.baswell.routes.RoutesConfiguration#routesMetaPath}. If {@code true}
   * is returned the Routes meta page will be displayed.
   *
   * @param servletRequest The HTTP request.
   * @param servletResponse The HTTP response
   * @return True if the Routes meta page is allowed to be shown, false otherwise.
   * @throws IOException  If an input or output error occurs while the servlet is handling the HTTP request.
   * @throws ServletException If the HTTP request cannot be handled.
   */
  boolean metaRequestAuthenticated(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, ServletException;
}
