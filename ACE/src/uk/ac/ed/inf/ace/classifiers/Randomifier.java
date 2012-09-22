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
package uk.ac.ed.inf.ace.classifiers;

import java.io.File;
import java.util.Random;
import java.util.Set;
import uk.ac.ed.inf.ace.*;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Randomifier extends ClassifierBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.Classifier> {

  private class RandomTrainer implements Trainer {

    private final Set<Object> labels;
    private final Random random;

    private RandomTrainer(Set<Object> labels, Random random) {
      this.labels = labels;
      this.random = random;
    }

    @Override
    public void add(ReadableDocument document, Object label) {
    }

    @Override
    public Model createModel() {
      return new RandomModel(labels, random);
    }
  }

  private class RandomModel implements Model {

    private final String[] labels;
    private final Random random;

    private RandomModel(Set<Object> labels, Random random) {
      this.labels = labels.toArray(new String[labels.size()]);
      this.random = random;
    }

    @Override
    public Object classify(ReadableDocument document, Object actualLabel, Task task)
        throws Exception {
      return labels[random.nextInt(labels.length)];
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {
    }
  }

  public Randomifier(Engine<?, ?> engine, uk.ac.ed.inf.ace.config.v1.Classifier config) {
    super(engine, config);
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new RandomTrainer(labels, randomSource.get(this));
  }
}