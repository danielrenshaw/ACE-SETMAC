/*
 * Copyright 2012 Daniel Renshaw &lt;d.renshaw@sms.ed.ac.uk&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ed.inf.ace.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DictionaryTest {

  @Test
  public void testSingleItem() {
    Dictionary dictionary = new Dictionary();
    Integer id = dictionary.get("foo");
    assertEquals("foo", dictionary.get(id));
    assertEquals(1, dictionary.size());
    assertNotNull(dictionary.tokens());
    assertNotNull(dictionary.ids());
    assertTrue(Sets.symmetricDifference(dictionary.tokens(), ImmutableSet.of("foo")).isEmpty());
    assertTrue(Sets.symmetricDifference(dictionary.ids(), ImmutableSet.of(id)).isEmpty());
  }

  @Test
  public void testMultipleItems() {
    Dictionary dictionary = new Dictionary();
    Integer id1 = dictionary.get("foo");
    Integer id2 = dictionary.get("bar");
    assertNotSame(id1, id2);
    assertEquals("foo", dictionary.get(id1));
    assertEquals("bar", dictionary.get(id2));
    assertEquals(2, dictionary.size());
    assertNotNull(dictionary.tokens());
    assertNotNull(dictionary.ids());
    assertTrue(
        Sets.symmetricDifference(dictionary.tokens(), ImmutableSet.of("foo", "bar")).isEmpty());
    assertTrue(Sets.symmetricDifference(dictionary.ids(), ImmutableSet.of(id1, id2)).isEmpty());
  }
}