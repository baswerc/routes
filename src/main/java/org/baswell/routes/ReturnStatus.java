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

/**
 * <p>
 * When thrown from {@link org.baswell.routes.BeforeRoute} or {@link org.baswell.routes.Route} methods {@HttpServletResponse.setStatus} will
 * be set with the {@code status} of this exception and no further processing will continue.
 * </p>
 */
public class ReturnStatus extends RuntimeException
{
  /**
   * <p>
   * Shortcut for returning 400.
   * </p>
   *
   * <pre>
   * throw ReturnHttpResponseStatus.BAD_REQUEST;
   * </pre>
   */
  public static ReturnStatus BAD_REQUEST_400 = new ReturnStatus(400);

  /**
   * <p>
   * Shortcut for returning 401.
   * </p>
   *
   * <pre>
   * throw ReturnHttpResponseStatus.UNAUTHORIZED;
   * </pre>
   */
  public static ReturnStatus UNAUTHORIZED_401 = new ReturnStatus(401);

  /**
   * <p>
   * Shortcut for returning 403.
   * </p>
   *
   * <pre>
   * throw ReturnHttpResponseStatus.FORBIDDEN;
   * </pre>
   */
  public static ReturnStatus FORBIDDEN_403 = new ReturnStatus(403);

  /**
   * <p>
   * Shortcut for returning 404.
   * </p>
   *
   * <pre>
   * throw ReturnHttpResponseStatus.NOT_FOUND;
   * </pre>
   */
  public static ReturnStatus NOT_FOUND_404 = new ReturnStatus(404);

  /**
   * <p>
   * Shortcut for returning 500.
   * </p>
   *
   * <pre>
   * throw ReturnHttpResponseStatus.INTERNAL_SERVER_ERROR;
   * </pre>
   */
  public static ReturnStatus INTERNAL_SERVER_ERROR_500 = new ReturnStatus(500);

  /**
   * The HTTP status to return.
   */
  public final int status;

  /**
   *
   * @param status The HTTP status to return.
   */
  public ReturnStatus(int status)
  {
    this.status = status;
  }

  public boolean isError()
  {
    return status >= 400;
  }
}
