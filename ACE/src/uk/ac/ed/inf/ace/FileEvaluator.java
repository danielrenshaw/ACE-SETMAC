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

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix.Result;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class FileEvaluator implements Evaluator {

  protected interface Constructor<T extends Evaluator> {

    T construct(Experiment experiment, ConfusionMatrix confusionMatrix);
  }

  protected static <T extends Evaluator> T read(Experiment experiment, File file,
      Constructor<T> constructor) throws Exception {
    Task task = experiment.getTask();
    ConfusionMatrix confusionMatrix = new ConfusionMatrix();
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;

    try {
      fileReader = new FileReader(file);
      bufferedReader = new BufferedReader(fileReader);
      String line = bufferedReader.readLine();
      String[] parts = line.split(",");
      String[] predictedLabels = new String[parts.length - 1];

      for (int index = 0; index < predictedLabels.length; index++) {
        predictedLabels[index] = parts[index + 1];
      }

      while ((line = bufferedReader.readLine()) != null) {
        parts = line.split(",");

        for (int index = 1; index < parts.length; index++) {
          confusionMatrix.add(new Result(task.parseLabel(parts[0]),
              task.parseLabel(predictedLabels[index - 1]), Integer.parseInt(parts[index])));
        }
      }
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }

      if (fileReader != null) {
        fileReader.close();
      }
    }

    return constructor.construct(experiment, confusionMatrix);
  }
  private final Experiment experiment;
  private final ConfusionMatrix confusionMatrix;
  private final Set<Object> labels;

  protected FileEvaluator(Experiment experiment) {
    this(experiment, new ConfusionMatrix());
  }

  protected FileEvaluator(Experiment experiment, ConfusionMatrix confusionMatrix) {
    this.experiment = experiment;
    this.confusionMatrix = confusionMatrix;
    this.labels = experiment.getTask().getLabels();
  }

  @Override
  public Experiment getExperiment() {
    return experiment;
  }

  @Override
  public void add(Object actualLabel, Object predictedLabel) {
    assert actualLabel.getClass().equals(predictedLabel.getClass());
    confusionMatrix.add(actualLabel, predictedLabel, 1);
  }

  @Override
  public void add(Result result) {
    confusionMatrix.add(result);
  }

  @Override
  public void output(File file) throws Exception {

    try (PrintStream printStream = new PrintStream(file)) {
      SetView<Object> allPredictedLabels = Sets.union(labels, confusionMatrix.getPredictedLabels());

      for (Object predictedLabel : allPredictedLabels) {
        printStream.print(",");
        printStream.print(predictedLabel);
      }

      printStream.println();

      for (Object actualLabel : Sets.union(labels, confusionMatrix.getActualLabels())) {
        printStream.print(actualLabel);

        for (Object predictedLabel : allPredictedLabels) {
          Integer value = confusionMatrix.get(actualLabel, predictedLabel);

          if (value == null) {
            value = 0;
          }

          printStream.print(",");
          printStream.print(value);
        }

        printStream.println();
      }
    }
  }

  @Override
  public double getFScore() {
    double precision = getPrecision();
    double recall = getRecall();
    return 2 * precision * recall / (precision + recall + Double.MIN_VALUE);
  }

  @Override
  public Iterator<Result> iterator() {
    return confusionMatrix.iterator();
  }

  protected Set<Object> getLabels() {
    return labels;
  }

  protected double getTruePositive(Object targetLabel) {
    Integer value = confusionMatrix.get(targetLabel, targetLabel);

    if (value == null) {
      return 0.0;
    } else {
      return value;
    }
  }

  protected double getTrueNegative(Object targetLabel) {
    int total = 0;

    for (Object actualLabel : labels) {
      assert targetLabel.getClass().equals(actualLabel.getClass());

      if (!actualLabel.equals(targetLabel)) {
        for (Object predictedLabel : labels) {
          assert targetLabel.getClass().equals(predictedLabel.getClass());

          if (!predictedLabel.equals(targetLabel)) {
            Integer value = confusionMatrix.get(actualLabel, predictedLabel);

            if (value != null) {
              total += value;
            }
          }
        }
      }
    }

    return total;
  }

  protected double getFalsePositive(Object targetLabel) {
    int total = 0;

    for (Object actualLabel : labels) {
      assert targetLabel.getClass().equals(actualLabel.getClass());

      if (!actualLabel.equals(targetLabel)) {
        Integer value = confusionMatrix.get(actualLabel, targetLabel);

        if (value != null) {
          total += value;
        }
      }
    }

    return total;
  }

  protected double getFalseNegative(Object targetLabel) {
    int total = 0;

    for (Object predictedLabel : labels) {
      assert targetLabel.getClass().equals(predictedLabel.getClass());

      if (!predictedLabel.equals(targetLabel)) {
        Integer value = confusionMatrix.get(targetLabel, predictedLabel);

        if (value != null) {
          total += value;
        }
      }
    }

    return total;
  }
}