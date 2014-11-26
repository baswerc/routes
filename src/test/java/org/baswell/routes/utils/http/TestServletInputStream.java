package org.baswell.routes.utils.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class TestServletInputStream extends ServletInputStream
{
  private InputStream source;
  
  public TestServletInputStream(InputStream source)
  {
    this.source = source;
  }

  @Override
  public int read() throws IOException
  {
    return source.read();
  }
}
