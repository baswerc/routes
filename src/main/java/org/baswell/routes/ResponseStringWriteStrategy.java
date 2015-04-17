package org.baswell.routes;

public enum ResponseStringWriteStrategy
{
  GSON,
  W3C_NODE,
  JAXB,
  DOM4J,
  JDOM,
  TO_STRING;

  ResponseStringWriteStrategy()
  {}
}
