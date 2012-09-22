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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.utils.ExecutableBase;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Experiment {

  private static final Logger LOGGER = Logger.getLogger(Experiment.class.getName());
  private final Engine<?, ?> engine;
  private final Programme programme;
  private final DataSet dataSet;
  private final DocType docType;
  private final Classifier classifier;
  private final Task task;
  private final int fold;
  private final int folds;
  private final File outputDirectory;
  private final File confusionMatrixFile;
  private final String groupName;
  private final String outputFilePrefix;
  private final String name;

  public Experiment(Engine<?, ?> engine, Programme programme, DataSet dataSet, DocType docType,
      Classifier classifier, Task task, int fold, int folds) throws Exception {
    this.engine = engine;
    this.programme = programme;
    this.dataSet = dataSet;
    this.docType = docType;
    this.classifier = classifier;
    this.task = task;
    this.fold = fold;
    this.folds = folds;
    this.groupName = MessageFormat.format("{0}-{1}-{2}-{3}-{4}", programme.getName(),
        dataSet.getName(), docType.getName(), classifier.getName(), task.getName());
    String foldName = "F" + fold;
    this.outputDirectory = new File(engine.getEnvironment().getOutputDirectory(), this.groupName);
    this.confusionMatrixFile = new File(this.outputDirectory, foldName + "-ConfusionMatrix.txt");
    this.name = this.groupName + "-" + foldName;
    this.outputFilePrefix = foldName + "-";
    this.outputDirectory.mkdirs();
  }

  public Task getTask() {
    return task;
  }

  public String getGroupName() {
    return groupName;
  }

  public File getOutputDirectory() {
    return outputDirectory;
  }

  public File getConfusionMatrixFile() throws Exception {
    return confusionMatrixFile;
  }

  public Callable<Boolean> getExecutable(RandomSource randomSource) {
    return new Executable(engine, randomSource);
  }

  private class Executable extends ExecutableBase<Boolean> {

    private final RandomSource randomSource;

    private Executable(Engine<?, ?> engine, RandomSource randomSource) {
      super(engine);
      this.randomSource = randomSource;
    }

    @Override
    public Boolean call() throws Exception {
      checkNotStopping();
      LOGGER.log(Level.INFO, "Starting experiment {0}", name);
      Trainer trainer = classifier.createTrainer(randomSource, task.getLabels());
      Iterable<Processor> preCacheProcessors = programme.getPreCacheProcessors();
      Iterable<Processor> postCacheProcessors = programme.getPostCacheProcessors();
      Iterator<ReadableDocument> documents = dataSet.getTrainingDocuments(randomSource, fold,
          folds, preCacheProcessors, postCacheProcessors, docType);
      boolean nothing = true;

      while (documents.hasNext()) {
        checkNotStopping();
        nothing = false;
        ReadableDocument document = documents.next();
        trainer.add(document, task.label(document));
      }

      if (nothing) {
        LOGGER.log(Level.WARNING, "Experiment has zero training documents {0}", name);
        return false;
      }

      nothing = true;
      Model model = trainer.createModel();
      model.write(getOutputDirectory(), outputFilePrefix);
      Evaluator evaluator = task.createEvaluator(Experiment.this);
      documents = dataSet.getValidationDocuments(randomSource, fold, folds, preCacheProcessors,
          postCacheProcessors);

      while (documents.hasNext()) {
        checkNotStopping();
        nothing = false;
        ReadableDocument document = documents.next();
        Object actual = task.label(document);
        Object predicted = model.classify(document, actual, task);
        assert actual.getClass().equals(predicted.getClass());
        evaluator.add(actual, predicted);

        if (!actual.equals(predicted)) {
          writeException(document.getId(), actual, predicted);
        }
      }

      if (nothing) {
        LOGGER.log(Level.WARNING, "Experiment has zero validation documents {0}", name);
        return false;
      }

      evaluator.output(getConfusionMatrixFile());
      LOGGER.log(Level.INFO, "Completed experiment {0}", name);
      return true;
    }
  }

  private static String clean(Object content) throws Exception {
    return content.toString().replace(",", " ").replace("\n", " ");
  }

  private void writeException(String id, Object actual, Object predicted) throws Exception {
    FileOutputStream fileOutputStream = new FileOutputStream(new File(outputDirectory,
        outputFilePrefix + "Misclassifications.txt"), true);
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");

    try {
      outputStreamWriter.write(String.format("%s,%s,%s,%s\n", actual, predicted, id,
          clean(docType.getDocument(id).getContent())));
    } finally {
      outputStreamWriter.flush();
      outputStreamWriter.close();
      fileOutputStream.flush();
      fileOutputStream.close();
    }
  }

  @Override
  public String toString() {
    return name;
  }
}