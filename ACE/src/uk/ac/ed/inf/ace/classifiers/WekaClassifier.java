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
package uk.ac.ed.inf.ace.classifiers;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import uk.ac.ed.inf.ace.ClassifierBase;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.ReadableDocument;
import uk.ac.ed.inf.ace.Task;
import uk.ac.ed.inf.ace.Trainer;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class WekaClassifier<E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.WekaClassifierBase>
    extends ClassifierBase<E, C> {

  private class WekaTrainer implements Trainer {

    private final Instances instances = constructInstances();

    @Override
    public void add(ReadableDocument document, Object label) throws Exception {
      Instance instance = constructInstance(document);
      instance.setDataset(instances);

      if (label == null) {
        instance.setClassMissing();
      } else {
        instance.setClassValue(label.toString());
      }

      instances.add(instance);
    }

    @Override
    public Model createModel() throws Exception {
      weka.classifiers.Classifier classifier = (weka.classifiers.Classifier) Class.forName(
          getConfig().getWekaType()).newInstance();
      classifier.buildClassifier(instances);
      return new WekaModel(instances, classifier);
    }
  }

  private class WekaModel implements Model {

    private final Instances instances;
    private final weka.classifiers.Classifier classifier;

    public WekaModel(Instances instances, Classifier classifier) {
      this.instances = new Instances(instances, 1);
      this.classifier = classifier;
    }

    @Override
    public Object classify(ReadableDocument document, Object actualLabel, Task task)
        throws Exception {
      Instance instance = constructInstance(document);
      instance.setDataset(instances);
      double classId = classifier.classifyInstance(instance);
      return task.parseLabel(instances.classAttribute().value((int) classId));
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {
      try (PrintWriter printWriter = new PrintWriter(new File(outputDirectory,
              outputFilePrefix + "Model.txt"))) {
        printWriter.write(classifier.toString());
      }
    }
  }

  public WekaClassifier(E engine, C config) {
    super(engine, config);
  }

  protected abstract Instances constructInstances();

  protected abstract Instance constructInstance(ReadableDocument document);

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) throws Exception {
    return new WekaTrainer();
  }
}
