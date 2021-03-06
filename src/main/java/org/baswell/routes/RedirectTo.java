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

import java.net.URL;

/**
 * <p>
 * When thrown from {@link org.baswell.routes.BeforeRoute} or {@link org.baswell.routes.Route} methods the current request
 * will immediately (no further processing) be redirected to the given {@code redirectUrl}. Any {@link AfterRoute} methods
 * for the current request will still be processed.
 * </p>
 */
public class RedirectTo extends RuntimeException
{
  public final String redirectUrl;

  /**
   * If {@code redirectUrl} is not a fully qualified URL then it will be considered relative from the current HTTP request.
   *
   * @param redirectUrl
   */
  public RedirectTo(String redirectUrl)
  {
    assert redirectUrl != null && !redirectUrl.isEmpty();
    
    this.redirectUrl = redirectUrl;
  }

  public RedirectTo(URL redirectUrl)
  {
    assert redirectUrl != null;

    this.redirectUrl = redirectUrl.toString();
  }
}
