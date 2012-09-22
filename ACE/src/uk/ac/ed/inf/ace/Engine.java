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

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.List;
import uk.ac.ed.inf.ace.utils.*;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Engine<E extends Engine<E, C>, C extends uk.ac.ed.inf.ace.config.v1.EngineBase>
    extends ConfiguredBase<E, C> {

  private final String environmentName;
  private final HardCache hardCache = new HardCache("Engine");
  private MainThread mainThread;
  private boolean stopping;
  private final SupplierEx<Environment<Engine<E, C>>> environmentSupplier =
      new SupplierEx<Environment<Engine<E, C>>>() {
        @Override
        public Environment<Engine<E, C>> get() throws Exception {
          return Environment.<Engine<E, C>>getActiveEnvironment(Engine.this, environmentName,
              getConfig().getEnvironments().getEnvironment(), Utilities.extractConfigs(
              getConfig().getDatabases().getDatabaseBase()));
        }
      };
  private final SupplierEx<List<DataSet>> dataSetsSupplier =
      new SupplierEx<List<DataSet>>() {
        @Override
        public List<DataSet> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getDataSets().getDataSetBase(),
              Engine.this, DataSet.class);
        }
      };
  private final SupplierEx<List<DocType>> docTypesSupplier =
      new SupplierEx<List<DocType>>() {
        @Override
        public List<DocType> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getDocTypes().getDocTypeBase(),
              Engine.this, DocType.class);
        }
      };
  private final SupplierEx<List<Classifier>> classifiersSupplier =
      new SupplierEx<List<Classifier>>() {
        @Override
        public List<Classifier> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getClassifiers().getClassifierBase(),
              Engine.this, Classifier.class);
        }
      };
  private final SupplierEx<List<Task>> tasksSupplier =
      new SupplierEx<List<Task>>() {
        @Override
        public List<Task> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getTasks().getTaskBase(), Engine.this,
              Task.class);
        }
      };
  private final SupplierEx<List<Processor>> preCacheProcessorsSupplier =
      new SupplierEx<List<Processor>>() {
        @Override
        public List<Processor> get() throws Exception {
          return Utilities.getConfiguredItems(
              getConfig().getPreCacheProcessors().getProcessorBase(), Engine.this, Processor.class);
        }
      };
  private final SupplierEx<List<Processor>> postCacheProcessorsSupplier =
      new SupplierEx<List<Processor>>() {
        @Override
        public List<Processor> get() throws Exception {
          return Utilities.getConfiguredItems(
              getConfig().getPostCacheProcessors().getProcessorBase(), Engine.this,
              Processor.class);
        }
      };
  private final SupplierEx<List<Programme>> programmesSupplier =
      new SupplierEx<List<Programme>>() {
        @Override
        public List<Programme> get() throws Exception {
          return Utilities.getConfiguredItems(getConfig().getProgrammes().getProgramme(),
              Engine.this, Programme.class);
        }
      };
  private final SupplierEx<ThreadPool> threadPoolSupplier =
      new SupplierEx<ThreadPool>() {
        @Override
        public ThreadPool get() throws Exception {
          return new ThreadPool(getEnvironment().getThreadPoolSize() + Iterables.size(
              getProgrammes()));
        }
      };
  private final SupplierEx<DocumentCache> cacheSupplier =
      new SupplierEx<DocumentCache>() {
        @Override
        public DocumentCache get() throws Exception {
          return new DocumentCache(getEnvironment());
        }
      };

  public Engine(C config, String environmentName) {
    super(null, config);
    this.environmentName = environmentName;
  }

  public Environment getEnvironment() throws Exception {
    return hardCache.get(environmentSupplier);
  }

  public Iterable<DataSet> getDataSets() throws Exception {
    return hardCache.get(dataSetsSupplier);
  }

  public Iterable<DocType> getDocTypes() throws Exception {
    return hardCache.get(docTypesSupplier);
  }

  public Iterable<Classifier> getClassifiers() throws Exception {
    return hardCache.get(classifiersSupplier);
  }

  public Iterable<Task> getTasks() throws Exception {
    return hardCache.get(tasksSupplier);
  }

  public Iterable<Processor> getPreCacheProcessors() throws Exception {
    return hardCache.get(preCacheProcessorsSupplier);
  }

  public Iterable<Processor> getPostCacheProcessors() throws Exception {
    return hardCache.get(postCacheProcessorsSupplier);
  }

  public Iterable<Programme> getProgrammes() throws Exception {
    return hardCache.get(programmesSupplier);
  }

  public ThreadPool getThreadPool() throws Exception {
    return hardCache.get(threadPoolSupplier);
  }

  public DocumentCache getDocumentCache() throws Exception {
    return hardCache.get(cacheSupplier);
  }

  public synchronized boolean isStopping() {
    return stopping;
  }

  public synchronized void start(boolean rebuildDatabase, boolean generateTestData,
      boolean databaseQueryMode, boolean analysisOnly) throws Exception {
    Preconditions.checkState(!stopping && mainThread == null, "An engine can only be started once");
    mainThread = new MainThread(this, rebuildDatabase, generateTestData, databaseQueryMode,
        analysisOnly);
    mainThread.start();
  }

  public void stop() throws Exception {
    synchronized (this) {
      stopping = true;
    }

    getThreadPool().stop();
  }
}