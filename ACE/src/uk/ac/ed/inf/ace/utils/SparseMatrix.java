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
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class SparseMatrix<K, V> extends HashMap<List<K>, V> {

  private final int dimensions;

  public SparseMatrix(int dimensions) {
    Preconditions.checkArgument(dimensions > 1);
    this.dimensions = dimensions;
  }

  public V put(K key1, K key2, V value) {
    return put(ImmutableList.<K>builder().add(key1).add(key2).build(), value);
  }

  public V put(K key1, K key2, K key3, V value) {
    return put(ImmutableList.<K>builder().add(key1).add(key2).add(key3).build(), value);
  }

  public V get(K key1, K key2) {
    return get(ImmutableList.<K>builder().add(key1).add(key2).build());
  }

  public V get(K key1, K key2, K key3) {
    return get(ImmutableList.<K>builder().add(key1).add(key2).add(key3).build());
  }

  @Override
  public V put(List<K> keys, V value) {
    Preconditions.checkState(keys.size() == dimensions, "Dimensions mismatch");
    return super.put(keys, value);
  }

  @Override
  public void putAll(Map<? extends List<K>, ? extends V> m) {
    Preconditions.checkState(Iterables.all(m.keySet(),
        new Predicate<List<K>>() {
          @Override
          public boolean apply(List<K> keys) {
            return keys.size() == dimensions;
          }
        }), "Dimensions mismatch");
    super.putAll(m);
  }
}