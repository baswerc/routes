package org.baswell.routes.utils.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class TestServletOutputStream extends ServletOutputStream
{
  final OutputStream outStream;

  public TestServletOutputStream(OutputStream outStream)
  {
    this.outStream = outStream;
  }

  @Override
  public void write(int b) throws IOException
  {
    outStream.write(b);
  }
}
