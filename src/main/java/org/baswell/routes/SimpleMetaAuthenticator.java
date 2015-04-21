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
 * A simple MetaAuthenticator that uses a static user name and password (provided at construction) and Base64 to determine if
 * access to the Routes meta page is allowed.
 */
public class SimpleMetaAuthenticator extends BasicMetaAuthenticator
{
  private final String userName;

  private final String password;

  public SimpleMetaAuthenticator(String userName, String password)
  {
    this.userName = userName;
    this.password = password;
  }

  @Override
  protected boolean validCredentials(String userName, String password)
  {
    return this.userName.equals(userName) && this.password.equals(password);
  }
}