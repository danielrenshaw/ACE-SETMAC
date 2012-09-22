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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import javax.xml.bind.JAXBElement;
import uk.ac.ed.inf.ace.utils.ExecutableBase;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;
import uk.ac.ed.inf.ace.utils.Utilities;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Programme extends NamedBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.Programme> {

  private final HardCache hardCache = new HardCache("Programme");
  private final SupplierEx<List<DataSet>> dataSetsSupplier =
      new SupplierEx<List<DataSet>>() {
        @Override
        public List<DataSet> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.DataSets collection = getConfig().getDataSets();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getDataSetBaseOrDataSetRef();
          return Utilities.getConfiguredItems(configs, getEngine(), DataSet.class,
              getEngine().getDataSets());
        }
      };
  private final SupplierEx<List<DocType>> docTypesSupplier =
      new SupplierEx<List<DocType>>() {
        @Override
        public List<DocType> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.DocTypes collection = getConfig().getDocTypes();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getDocTypeBaseOrDocTypeRef();
          return Utilities.getConfiguredItems(configs, getEngine(), DocType.class,
              getEngine().getDocTypes());
        }
      };
  private final SupplierEx<List<Classifier>> classifiersSupplier =
      new SupplierEx<List<Classifier>>() {
        @Override
        public List<Classifier> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.Classifiers collection =
              getConfig().getClassifiers();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getClassifierBaseOrClassifierRef();
          return Utilities.getConfiguredItems(configs, getEngine(), Classifier.class,
              getEngine().getClassifiers());
        }
      };
  private final SupplierEx<List<Task>> tasksSupplier =
      new SupplierEx<List<Task>>() {
        @Override
        public List<Task> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.Tasks collection = getConfig().getTasks();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getTaskBaseOrTaskRef();
          return Utilities.getConfiguredItems(configs, getEngine(), Task.class,
              getEngine().getTasks());
        }
      };
  private final SupplierEx<List<Processor>> preCacheProcessorsSupplier =
      new SupplierEx<List<Processor>>() {
        @Override
        public List<Processor> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.PreCacheProcessors collection =
              getConfig().getPreCacheProcessors();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getProcessorBaseOrProcessorRef();
          return Utilities.getConfiguredItems(configs, getEngine(), Processor.class,
              getEngine().getPreCacheProcessors());
        }
      };
  private final SupplierEx<List<Processor>> postCacheProcessorsSupplier =
      new SupplierEx<List<Processor>>() {
        @Override
        public List<Processor> get() throws Exception {
          uk.ac.ed.inf.ace.config.v1.Programme.PostCacheProcessors collection =
              getConfig().getPostCacheProcessors();
          List<JAXBElement<? extends uk.ac.ed.inf.ace.config.v1.Enableable>> configs =
              collection == null ? null : collection.getProcessorBaseOrProcessorRef();
          return Utilities.getConfiguredItems(configs, getEngine(), Processor.class,
              getEngine().getPostCacheProcessors());
        }
      };
  private final SupplierEx<List<Experiment>> experimentsSupplier =
      new SupplierEx<List<Experiment>>() {
        @Override
        public List<Experiment> get() throws Exception {
          Engine<?, ?> engine = getEngine();
          int folds = Utilities.ifNull(getConfig().getCrossValidationFolds(),
              engine.getEnvironment().getCrossValidationFolds());
          Builder<Experiment> builder = ImmutableList.builder();

          for (DataSet dataSet : getDataSets()) {
            for (DocType docType : getDocTypes()) {
              for (int fold = 0; fold < folds; fold++) {
                for (Classifier classifier : getClassifiers()) {
                  for (Task task : getTasks()) {
                    builder.add(new Experiment(engine, Programme.this, dataSet, docType,
                        classifier, task, fold, folds));
                  }
                }
              }
            }
          }

          return builder.build();
        }
      };

  public Programme(Engine<?, ?> engine, uk.ac.ed.inf.ace.config.v1.Programme config) {
    super(engine, config);
  }

  private List<DataSet> getDataSets() throws Exception {
    return hardCache.get(dataSetsSupplier);
  }

  private List<DocType> getDocTypes() throws Exception {
    return hardCache.get(docTypesSupplier);
  }

  private List<Classifier> getClassifiers() throws Exception {
    return hardCache.get(classifiersSupplier);
  }

  private List<Task> getTasks() throws Exception {
    return hardCache.get(tasksSupplier);
  }

  public Iterable<Processor> getPreCacheProcessors() throws Exception {
    return hardCache.get(preCacheProcessorsSupplier);
  }

  public Iterable<Processor> getPostCacheProcessors() throws Exception {
    return hardCache.get(postCacheProcessorsSupplier);
  }

  public Iterable<Experiment> getExperiments() throws Exception {
    return hardCache.get(experimentsSupplier);
  }

  @Override
  public boolean isValid() throws Exception {
    return !getDataSets().isEmpty() && !getDocTypes().isEmpty() && !getClassifiers().isEmpty()
        && !getTasks().isEmpty();
  }

  public Callable<Boolean> getExecutable(RandomSource randomSource, boolean analysisOnly) {
    Long randomSeed = getConfig().getRandomSeed();

    if (randomSeed != null) {
      randomSource = new RandomSource(randomSeed);
    }

    return new Executable(getEngine(), randomSource, analysisOnly);
  }

  private class Executable extends ExecutableBase<Boolean> {

    private final RandomSource randomSource;
    private final boolean analysisOnly;

    private Executable(Engine<?, ?> engine, RandomSource randomSource, boolean analysisOnly) {
      super(engine);
      this.randomSource = randomSource;
      this.analysisOnly = analysisOnly;
    }

    @Override
    public Boolean call() throws Exception {
      if (!analysisOnly) {
        CompletionService<Boolean> completionService =
            new ExecutorCompletionService<>(getEngine().getThreadPool());
        int queuedTasks = 0;

        for (Experiment experiment : getExperiments()) {
          checkNotStopping();
          completionService.submit(experiment.getExecutable(randomSource));
          queuedTasks++;
        }

        for (int index = 0; index < queuedTasks; index++) {
          checkNotStopping();
          completionService.take().get();
        }
      }

      return ResultsAnalyser.analyse(getEngine(), Programme.this);
    }
  }
}