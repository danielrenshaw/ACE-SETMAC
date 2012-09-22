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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class InstrumentedCache<K, V> {

  private static final Logger LOGGER = Logger.getLogger(InstrumentedCache.class.getName());
  private static final int REQUEST_COUNT_EMIT_GAP = 10_000;
  private final AtomicInteger requestCount = new AtomicInteger();
  private final String name;
  private final LoadingCache<K, V> cache;

  protected InstrumentedCache(String name, final LoadingCache<K, V> cache) {
    this.name = name;
    this.cache = CacheBuilder.newBuilder().recordStats().build(new CacheLoader<K, V>() {

      @Override
      public V load(K key) throws Exception {
        return cache.get(key);
      }
    });
  }

  private void emitStats() {
    int thisRequestCount = requestCount.getAndIncrement();

    if (thisRequestCount > 0 && thisRequestCount % REQUEST_COUNT_EMIT_GAP == 0) {
      CacheStats stats = cache.stats();
      LOGGER.log(Level.INFO, "{0} stats (C-H-M-E-S-F): {1} - {2} - {3} - {4} - {5} - {6}",
          new Object[]{name, thisRequestCount, stats.hitCount(), stats.missCount(),
            stats.evictionCount(), stats.loadSuccessCount(), stats.loadExceptionCount()});
    }
  }

  /**
   * Does the same as {@link InstrumentedCache#get(Object)} but
   * will wrap any exception in an unchecked {@code RuntimeException}. Avoid using this method
   * whenever possible.
   * @param query The details of the document to returned (after loading if required)
   * @return The requested document.
   */
  protected V getUnchecked(K query) {
    emitStats();
    return cache.getUnchecked(query);
  }

  /**
   * @param query The details of the document to returned (after loading if required)
   * @return The requested document.
   */
  protected V get(K query) throws Exception {
    emitStats();
    return cache.get(query);
  }

  public void invalidateAll() {
    cache.invalidateAll();
  }
}