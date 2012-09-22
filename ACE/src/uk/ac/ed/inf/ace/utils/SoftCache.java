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
import java.util.concurrent.TimeUnit;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class SoftCache extends InstrumentedCache<SupplierEx<?>, Object> {

  public SoftCache(String name, int evictAfterAccessMinutes, int maximumSize) {
    super("Documents",
        CacheBuilder.newBuilder().concurrencyLevel(1).expireAfterAccess(
        evictAfterAccessMinutes, TimeUnit.MINUTES).maximumSize(maximumSize).build(
        new CacheLoader<SupplierEx<?>, Object>() {

          @Override
          public Object load(SupplierEx<?> key) throws Exception {
            return key.get();
          }
        }));
  }

  @SuppressWarnings("unchecked")
  public <T> T get(SupplierEx<T> supplier) throws Exception {
    return (T) super.get(supplier);
  }

  @SuppressWarnings("unchecked")
  public <T> T getUnchecked(SupplierEx<T> supplier) {
    return (T) super.getUnchecked(supplier);
  }
}