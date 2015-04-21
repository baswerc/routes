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
 * If thrown from {@link org.baswell.routes.BeforeRoute} or {@link org.baswell.routes.Route} methods the current request
 * will immediately (no further processing) be returned the given {@link #code}. This exception should not be thrown
 * from {@link org.baswell.routes.AfterRoute} methods since the HTTP response has already been processed.
 */
public class ReturnHttpResponseCode extends RuntimeException
{
  public static ReturnHttpResponseCode OK = new ReturnHttpResponseCode(200);

  public static ReturnHttpResponseCode BAD_REQUEST = new ReturnHttpResponseCode(400);

  public static ReturnHttpResponseCode UNAUTHORIZED = new ReturnHttpResponseCode(401);

  public static ReturnHttpResponseCode NOT_FOUND = new ReturnHttpResponseCode(404);

  public static ReturnHttpResponseCode INTERNAL_SERVER_ERROR = new ReturnHttpResponseCode(500);

  public final int code;
  
  public ReturnHttpResponseCode(int code)
  {
    this.code = code;
  }
}
