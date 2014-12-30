package org.baswell.routes.meta;

public class SimpleAuthenticator extends BasicMetaAuthenticator
{
  private final String userName;

  private final String password;

  public SimpleAuthenticator(String userName, String password)
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