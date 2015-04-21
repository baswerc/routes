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
