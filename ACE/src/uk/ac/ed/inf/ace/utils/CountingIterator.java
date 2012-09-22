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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class CountingIterator<T> implements Iterator<T> {

  private static final Logger LOGGER = Logger.getLogger(CountingIterator.class.getName());
  private final Iterator<T> source;
  private final String name;
  private int count;

  public CountingIterator(Iterator<T> source, String name) {
    this.source = source;
    this.name = name;
  }

  @Override
  public boolean hasNext() {
    return source.hasNext();
  }

  @Override
  public T next() {
    count++;
    LOGGER.log(Level.INFO, "{0}; count={1}", new Object[]{name, count});
    return source.next();
  }

  @Override
  public void remove() {
    source.remove();
  }
}