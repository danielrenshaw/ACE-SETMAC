/*
 * Copyright 2012 Daniel.
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
package uk.ac.ed.inf.ace.processors;

import java.util.Iterator;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.ProcessorBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.utils.Dictionary;
import uk.ac.ed.inf.ace.utils.DictionaryIterable;
import uk.ac.ed.inf.ace.utils.SoftCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;

/**
 *
 * @author Daniel
 */
public class CompressByDictionary extends ProcessorBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.Processor> {
  private final SoftCache softCache;

  public CompressByDictionary(Engine<?, ?> engine, uk.ac.ed.inf.ace.config.v1.Processor config) throws Exception {
    super(engine, config);
    this.softCache = new SoftCache("DataSetDictionaries", getEngine().getEnvironment().getCacheEvictAfterAccess(), 1);
  }

  private final SupplierEx<Dictionary> dictionarySupplier =
      new SupplierEx<Dictionary>() {
        @Override
        public Dictionary get() throws Exception {
          return new Dictionary();
        }
      };

  @Override
  protected ReadWriteableDocument process(ReadWriteableDocument input) throws Exception {
    @SuppressWarnings("unchecked")
    Iterator<String> content = (Iterator<String>) input.getContent();
    input.setContent(new DictionaryIterable(softCache.get(dictionarySupplier), content));
    return input;
  }
}
