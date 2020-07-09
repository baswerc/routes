package org.baswell.routes;

/**
 * Throw this from any @BeforeRoute, @Route or @AfterRoute to immediately halt the pipeline execution of the current request. When thrown it's
 * assumed that the response to the client has already been taken care of.
 */
public class HaltPipeline extends RuntimeException
{
  public HaltPipeline()
  {}
}
