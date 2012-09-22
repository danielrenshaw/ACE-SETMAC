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
import uk.ac.ed.inf.ace.utils.ConfusionMatrix;

/**
 * Produces performance measures for a binary classification experiment. Expects a class label
 * "true" (Java boolean type) and assumes only one other class label exists, i.e. "false". Adds
 * {@link java.lang.Double#MIN_VALUE} to all denominators to avoid divide by zero issues.
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class BinaryEvaluator extends FileEvaluator {

  public BinaryEvaluator(Experiment experiment) {
    super(experiment);
  }

  private BinaryEvaluator(Experiment experiment, ConfusionMatrix confusionMatrix) {
    super(experiment, confusionMatrix);
  }

  public static BinaryEvaluator read(Experiment experiment, File file) throws Exception {
    return BinaryEvaluator.read(experiment, file, new Constructor<BinaryEvaluator>() {
      @Override
      public BinaryEvaluator construct(Experiment experiment, ConfusionMatrix confusionMatrix) {
        return new BinaryEvaluator(experiment, confusionMatrix);
      }
    });
  }

  /**
   * @return (TP + TN) / (TP + TN + FN + FP) with respect to the class label "true".
   */
  @Override
  public double getAccuracy() {
    double tp = getTruePositive(true);
    double tn = getTrueNegative(true);
    return (tp + tn) / (tp + tn + getFalseNegative(true) + getFalsePositive(true)
        + Double.MIN_VALUE);
  }

  /**
   * @return TP / (TP + FP + {@link java.lang.Double#MIN_VALUE}) with respect to the class label
   * "true".
   */
  @Override
  public double getPrecision() {
    double tp = getTruePositive(true);
    return tp / (tp + getFalsePositive(true) + Double.MIN_VALUE);
  }

  /**
   * @return TP / (TP + FN + {@link java.lang.Double#MIN_VALUE}) with respect to the class label
   * "true".
   */
  @Override
  public double getRecall() {
    double tp = getTruePositive(true);
    return tp / (tp + getFalseNegative(true) + Double.MIN_VALUE);
  }

  /**
   * @return TN / (TN + FP + {@link java.lang.Double#MIN_VALUE}) with respect to the class label
   * "true".
   */
  @Override
  public double getSpecificity() {
    double tn = getTrueNegative(true);
    return tn / (tn + getFalsePositive(true) + Double.MIN_VALUE);
  }

  /**
   * @return TN / (TN + FN + {@link java.lang.Double#MIN_VALUE}) with respect to the class label
   * "true".
   */
  @Override
  public double getNegativePredictiveValue() {
    double tn = getTrueNegative(true);
    return tn / (tn + getFalseNegative(true) + Double.MIN_VALUE);
  }
}