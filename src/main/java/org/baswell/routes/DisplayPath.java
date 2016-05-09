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
 * When thrown from {@link BeforeRoute} or {@link Route} methods the current request
 * will immediately (no further processing) be directed to the given {@code path}. Any {@link AfterRoute} methods
 * for the current request will still be processed.
 * </p>
 */
public class DisplayPath extends RuntimeException
{
  public final String pathToDisplay;

  /**
   * @param pathToDisplay
   */
  public DisplayPath(String pathToDisplay)
  {
    assert pathToDisplay != null && !pathToDisplay.isEmpty();

    this.pathToDisplay = pathToDisplay;
  }
}