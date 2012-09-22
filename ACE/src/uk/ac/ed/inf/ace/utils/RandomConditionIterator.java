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

import java.util.Iterator;
import java.util.Random;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class RandomConditionIterator<T> implements Iterator<T> {

  private final Iterator<T> source;
  private final Random random;
  private T buffer;

  protected RandomConditionIterator(Iterator<T> source, long randomSeed) {
    this.source = source;
    this.random = new Random(randomSeed);
  }

  protected abstract boolean condition(T item, Random random);

  @Override
  public boolean hasNext() {
    if (buffer == null) {
      while (source.hasNext()) {
        T item = source.next();

        if (condition(item, random)) {
          buffer = item;
          return true;
        }
      }

      return false;
    } else {
      return true;
    }
  }

  @Override
  public T next() {
    if (buffer == null) {
      while (true) {
        T item = source.next();

        if (condition(item, random)) {
          return item;
        }
      }
    } else {
      T item = buffer;
      buffer = null;
      return item;
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}