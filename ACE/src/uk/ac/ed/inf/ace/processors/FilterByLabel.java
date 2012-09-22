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
package uk.ac.ed.inf.ace.processors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Random;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.ProcessorBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.config.v1.FilterByLabel.Rule;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class FilterByLabel extends ProcessorBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.FilterByLabel> {

  private final HardCache hardCache = new HardCache("FilterByLabel");
  private final SupplierEx<Map<Object, Double>> sampleProbabilitiesSupplier =
      new SupplierEx<Map<Object, Double>>() {

        @Override
        public Map<Object, Double> get() throws Exception {
          Builder<Object, Double> rulesBuilder = ImmutableMap.builder();

          for (Rule rule : getConfig().getRule()) {
            rulesBuilder.put(rule.getLabel(), rule.getSampleProbability().doubleValue());
          }

          return rulesBuilder.build();
        }
      };

  public FilterByLabel(Engine<?, ?> engine, uk.ac.ed.inf.ace.config.v1.FilterByLabel config) {
    super(engine, config);
  }

  @Override
  public ReadWriteableDocument process(ReadWriteableDocument document, Random random)
      throws Exception {
    Object label = document.getProperties().get(getConfig().getLabelPropertyKey());
    Double sampleProbability = hardCache.get(sampleProbabilitiesSupplier).get(label);

    if (sampleProbability == null) {
      sampleProbability = 1.0;
    }

    return random.nextDouble() < sampleProbability ? document : null;
  }
}