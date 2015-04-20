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