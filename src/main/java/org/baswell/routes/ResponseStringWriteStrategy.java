package org.baswell.routes;

public enum ResponseStringWriteStrategy
{
  GSON,
  W3C_NODE,
  JAXB,
  JDOM2_DOCUMENT,
  JDOM2_ELEMENT,
  TO_STRING;

  ResponseStringWriteStrategy()
  {}
}
