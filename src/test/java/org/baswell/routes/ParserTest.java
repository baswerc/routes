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

import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest
{
  @Test
  public void testSimplePath()
  {
    TreeParser treeParser = new TreeParser();
    
    ParsedRouteTree tree = treeParser.parse("/test/this/out");
    
    assertEquals(tree.pathTerminals.size(), 3);
    assertEquals(tree.parameterTerminals.size(), 0);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ParsedExactPathTerminal);
    ParsedExactPathTerminal exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
  }

  @Test
  public void testSimplePathParameters()
  {
    TreeParser treeParser = new TreeParser();
    
    ParsedRouteTree tree = treeParser.parse("/test/this/out?one=two&three=four");
    
    assertEquals(tree.pathTerminals.size(), 3);
    
    assertTrue(tree.pathTerminals.get(0) instanceof ParsedExactPathTerminal);
    ParsedExactPathTerminal exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(0);
    assertEquals(exactPathTerminal.routeIndex, 0);
    assertEquals(exactPathTerminal.pathIndex, 0);
    assertEquals(exactPathTerminal.segment, "test");
    
    assertTrue(tree.pathTerminals.get(1) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(1);
    assertEquals(exactPathTerminal.routeIndex, 1);
    assertEquals(exactPathTerminal.pathIndex, 1);
    assertEquals(exactPathTerminal.segment, "this");
    
    assertTrue(tree.pathTerminals.get(2) instanceof ParsedExactPathTerminal);
    exactPathTerminal = (ParsedExactPathTerminal)tree.pathTerminals.get(2);
    assertEquals(exactPathTerminal.routeIndex, 2);
    assertEquals(exactPathTerminal.pathIndex, 2);
    assertEquals(exactPathTerminal.segment, "out");
    
    assertEquals(tree.parameterTerminals.size(), 2);
    
    assertTrue(tree.parameterTerminals.get(0) instanceof ParsedExactParameterTerminal);
    ParsedExactParameterTerminal exactParameterTermainl = (ParsedExactParameterTerminal)tree.parameterTerminals.get(0);
    assertEquals(exactParameterTermainl.routeIndex, 3);
    assertEquals(exactParameterTermainl.name, "one");
    assertEquals(exactParameterTermainl.value, "two");

    assertTrue(tree.parameterTerminals.get(1) instanceof ParsedExactParameterTerminal);
    exactParameterTermainl = (ParsedExactParameterTerminal)tree.parameterTerminals.get(1);
    assertEquals(exactParameterTermainl.routeIndex, 4);
    assertEquals(exactParameterTermainl.name, "three");
    assertEquals(exactParameterTermainl.value, "four");

  }

}
