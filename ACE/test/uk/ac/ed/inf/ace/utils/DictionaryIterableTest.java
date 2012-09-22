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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DictionaryIterableTest {

  private static final Iterator<String> noContent = new Iterator<String>() {

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public String next() {
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  };
  private static final Iterable<String> someContent =
      ImmutableList.of("b", "d", "a", "b", "d", "c", "c", "d", "c", "d");
  private static final Set<String> tokens = ImmutableSet.of("a", "b", "c", "d");
  private static final Set<Integer> ids = ImmutableSet.of(0, 1, 2, 3);

  @Test(expected = NullPointerException.class)
  public void testNullDictionary() {
    DictionaryIterable instance = new DictionaryIterable(null, noContent);
  }

  @Test(expected = NullPointerException.class)
  public void testNullTokens() {
    DictionaryIterable instance = new DictionaryIterable(new Dictionary(), null);
  }

  @Test
  public void testNoContentHasNext() {
    DictionaryIterable instance = new DictionaryIterable(new Dictionary(), noContent);
    Iterator<String> iterator = instance.iterator();
    assertNotNull(iterator);
    assertEquals(false, iterator.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testNoContentNext() {
    DictionaryIterable instance = new DictionaryIterable(new Dictionary(), noContent);
    Iterator<String> iterator = instance.iterator();
    assertNotNull(iterator);
    iterator.next();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testRemove() {
    DictionaryIterable instance = new DictionaryIterable(new Dictionary(), someContent.iterator());
    Iterator<String> iterator = instance.iterator();
    assertNotNull(iterator);
    iterator.next();
    iterator.remove();
  }

  @Test
  public void testSomeContent() {
    Dictionary dictionary = new Dictionary();
    DictionaryIterable instance = new DictionaryIterable(dictionary, someContent.iterator());
    Iterator<String> iterator = instance.iterator();
    assertNotNull(iterator);
    assertEquals(4, dictionary.size());
    assertTrue(Sets.symmetricDifference(dictionary.tokens(), tokens).isEmpty());
    assertTrue(Sets.symmetricDifference(dictionary.ids(), ids).isEmpty());
    Map<String, Integer> counts = Maps.newHashMap();
    counts.put("a", 0);
    counts.put("b", 0);
    counts.put("c", 0);
    counts.put("d", 0);

    for (int index = 0; index < 10; index++) {
      assertEquals(true, iterator.hasNext());
      String token = iterator.next();
      assertNotNull(token);
      counts.put(token, counts.get(token) + 1);
    }

    assertFalse(iterator.hasNext());
    assertEquals(1, (int) counts.get("a"));
    assertEquals(2, (int) counts.get("b"));
    assertEquals(3, (int) counts.get("c"));
    assertEquals(4, (int) counts.get("d"));
  }
}