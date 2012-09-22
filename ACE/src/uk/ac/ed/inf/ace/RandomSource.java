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
package uk.ac.ed.inf.ace;

import java.util.Random;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class RandomSource {

  private final Random random;
  private final HardCache hardCache = new HardCache("RandomSource");

  private class SeedSupplier implements SupplierEx<Long> {

    private final Object key;

    private SeedSupplier(Object key) {
      this.key = key;
    }

    @Override
    public Long get() throws Exception {
      return random.nextLong();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final SeedSupplier other = (SeedSupplier) obj;
      if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 43 * hash + (this.key != null ? this.key.hashCode() : 0);
      return hash;
    }
  }

  public RandomSource(long seed) {
    this.random = new Random(seed);
  }

  public Random get(Object key) throws Exception {
    return new Random(hardCache.get(new SeedSupplier(key)));
  }
}