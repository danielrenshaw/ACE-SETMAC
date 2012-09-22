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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;

/**
 * Compresses a set of tokens into a set of identifiers using a {@link Dictionary} and provides
 * access to an iterator that will decompress the identifiers back into tokens. The order of the
 * tokens is not preserved but the quantity of each token is retained.
 * A possible optimization might be possible if consumers of this class could be amended to accept
 * {@code Map.Entry<String, Integer>} instances instead of requiring every occurrence of each token
 * to be enumerated.
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DictionaryIterable implements Iterable<String> {

  private class DecompressingIterator implements Iterator<String> {

    private final Iterator<Map.Entry<Integer, Integer>> source = counts.entrySet().iterator();
    private String bufferToken;
    private int bufferCount;

    @Override
    public boolean hasNext() {
      if (bufferToken == null) {
        return source.hasNext();
      } else {
        return true;
      }
    }

    @Override
    public String next() {
      if (bufferToken == null) {
        Map.Entry<Integer, Integer> entry = source.next();
        String token = dictionary.get(entry.getKey());
        Integer count = entry.getValue();

        if (count > 1) {
          bufferToken = token;
          bufferCount = count - 1;
        }

        return token;
      } else if (bufferCount == 1) {
        String temp = bufferToken;
        bufferToken = null;
        return temp;
      } else {
        bufferCount--;
        return bufferToken;
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  private final Dictionary dictionary;
  private final Map<Integer, Integer> counts;

  /**
   * The tokens are compressed immediately. The iterator will be exhausted after this constructor is
   * called.
   * @param dictionary The dictionary to use for mapping between tokens and identifiers.
   * @param tokens The tokens to be compressed. Null tokens are skipped.
   */
  public DictionaryIterable(Dictionary dictionary, Iterator<String> tokens) {
    Preconditions.checkNotNull(dictionary);
    Preconditions.checkNotNull(tokens);
    this.dictionary = dictionary;
    Map<Integer, Integer> temp = Maps.newHashMap();

    while (tokens.hasNext()) {
      String token = tokens.next();

      if (token == null) {
        continue;
      }

      int id = dictionary.get(token);
      Integer count = temp.get(id);

      if (count == null) {
        temp.put(id, 1);
      } else {
        temp.put(id, count + 1);
      }
    }
    
    this.counts = ImmutableMap.copyOf(temp);
  }

  @Override
  public Iterator<String> iterator() {
    return new DecompressingIterator();
  }
}