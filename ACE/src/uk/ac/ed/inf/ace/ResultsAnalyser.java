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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix.Result;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ResultsAnalyser {

  private ResultsAnalyser() {
  }

  public static boolean analyse(Engine<?, ?> engine, Programme programme)
      throws Exception {
    Map<String, List<Evaluator>> groups = Maps.newHashMap();

    for (Experiment experiment : programme.getExperiments()) {
      String groupName = experiment.getGroupName();
      List<Evaluator> evaluators = groups.get(groupName);
      File file = experiment.getConfusionMatrixFile();

      if (!file.exists()) {
        continue;
      }

      if (evaluators == null) {
        evaluators = Lists.newArrayList();
        groups.put(groupName, evaluators);
      }

      evaluators.add(experiment.getTask().readEvaluator(experiment, file));
    }

    for (Entry<String, List<Evaluator>> entry : groups.entrySet()) {
      String groupName = entry.getKey();
      List<Evaluator> evaluators = entry.getValue();
      Experiment experiment = evaluators.get(0).getExperiment();
      Evaluator aggregateEvaluator = experiment.getTask().createEvaluator(experiment);

      for (Evaluator evaluator : evaluators) {
        for (Result result : evaluator) {
          aggregateEvaluator.add(result);
        }
      }

      aggregateEvaluator.output(new File(engine.getEnvironment().getOutputDirectory(),
          groupName + "-ConfusionMatrix.txt"));
      File resultsFile = engine.getEnvironment().getResultsFile();

      synchronized (resultsFile) {

        try (FileWriter fileWriter = new FileWriter(resultsFile, true)) {
          writeResult(fileWriter, groupName, "ACC", aggregateEvaluator.getAccuracy());
          writeResult(fileWriter, groupName, "PRE", aggregateEvaluator.getPrecision());
          writeResult(fileWriter, groupName, "REC", aggregateEvaluator.getRecall());
          writeResult(fileWriter, groupName, "FSC", aggregateEvaluator.getFScore());
          writeResult(fileWriter, groupName, "NPV",
              aggregateEvaluator.getNegativePredictiveValue());
          writeResult(fileWriter, groupName, "SPE", aggregateEvaluator.getSpecificity());
        }
      }
    }

    return true;
  }

  private static void writeResult(FileWriter fileWriter, String group, String key, double value)
      throws Exception {
    fileWriter.write(group.replace("-", ","));
    fileWriter.write(",");
    fileWriter.write(key);
    fileWriter.write(",");
    fileWriter.write(Double.toString(value));
    fileWriter.write("\n");
  }
}