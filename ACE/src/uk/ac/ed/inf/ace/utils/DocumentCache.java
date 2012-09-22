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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import uk.ac.ed.inf.ace.Environment;
import uk.ac.ed.inf.ace.Processor;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.ReadableDocument;

/**
 * Provides a mechanism for storing processed documents for future use, avoiding the need to reload
 * and reprocess documents. Items are evicted using a least-recently-used policy once the maximum
 * capacity is reached.
 * Cache statistics are emitted to the log on every 10,000'th request. The letters C-H-M-E-S-F refer
 * to (request) Count, Hits, Misses, Evictions, (load) Successes, (load) Failures.
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DocumentCache extends InstrumentedCache<DocumentCacheQuery, ReadableDocument> {

  private static class Loader extends CacheLoader<DocumentCacheQuery, ReadableDocument> {

    @Override
    public ReadableDocument load(DocumentCacheQuery query) throws Exception {
      ReadWriteableDocument document = query.getDocType().getDocument(query.getDocumentId());

      for (Processor processor : query.getProcessors()) {
        document = processor.process(document, query.getRandom());

        if (document == null) {
          return null;
        }
      }
      
      return query.getReadableDocument(document);
    }
  }

  /**
   * @param environment The environment from which the degree of concurrency and cache capacity will
   *     be determined.
   */
  public DocumentCache(Environment<?> environment) {
    super("Documents",
        CacheBuilder.newBuilder().concurrencyLevel(
        environment.getThreadPoolSize()).maximumSize(
        environment.getCacheCapacity()).expireAfterAccess(environment.getCacheEvictAfterAccess(),
        TimeUnit.MINUTES).build(new Loader()));
  }
}