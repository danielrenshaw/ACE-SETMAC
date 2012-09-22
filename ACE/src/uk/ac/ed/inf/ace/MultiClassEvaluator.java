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

import java.io.File;
import java.util.Set;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class MultiClassEvaluator extends FileEvaluator {

  public MultiClassEvaluator(Experiment experiment) {
    super(experiment);
  }

  private MultiClassEvaluator(Experiment experiment, ConfusionMatrix confusionMatrix) {
    super(experiment, confusionMatrix);
  }

  public static MultiClassEvaluator read(Experiment experiment, File file) throws Exception {
    return MultiClassEvaluator.read(experiment, file, new Constructor<MultiClassEvaluator>() {

      @Override
      public MultiClassEvaluator construct(Experiment experiment,
          ConfusionMatrix confusionMatrix) {
        return new MultiClassEvaluator(experiment, confusionMatrix);
      }
    });
  }

  @Override
  public double getAccuracy() {
    double total = 0.0;
    Set<Object> labels = getLabels();

    for (Object label : labels) {
      double tp = getTruePositive(label);
      double tn = getTrueNegative(label);
      total += (tp + tn) / (tp + tn + getFalseNegative(label) + getFalsePositive(label)
          + Double.MIN_VALUE);
    }

    return total / labels.size();
  }

  @Override
  public double getPrecision() {
    double total = 0.0;
    Set<Object> labels = getLabels();

    for (Object label : labels) {
      double tp = getTruePositive(label);
      total += tp / (tp + getFalsePositive(label) + Double.MIN_VALUE);
    }

    return total / labels.size();
  }

  @Override
  public double getRecall() {
    double total = 0.0;
    Set<Object> labels = getLabels();

    for (Object label : labels) {
      double tp = getTruePositive(label);
      total += tp / (tp + getFalseNegative(label) + Double.MIN_VALUE);
    }

    return total / labels.size();
  }

  @Override
  public double getSpecificity() {
    double total = 0.0;
    Set<Object> labels = getLabels();

    for (Object label : labels) {
      double tn = getTrueNegative(label);
      total += tn / (tn + getFalsePositive(label) + Double.MIN_VALUE);
    }

    return total / labels.size();
  }

  @Override
  public double getNegativePredictiveValue() {
    double total = 0.0;
    Set<Object> labels = getLabels();

    for (Object label : labels) {
      double tn = getTrueNegative(label);
      total += tn / (tn + getFalseNegative(label) + Double.MIN_VALUE);
    }

    return total / labels.size();
  }
}