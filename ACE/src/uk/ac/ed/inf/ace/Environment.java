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
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.utils.HardCache;
import uk.ac.ed.inf.ace.utils.SupplierEx;
import uk.ac.ed.inf.ace.utils.Utilities;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Environment<E extends Engine<?, ?>>
    extends NamedBase<E, uk.ac.ed.inf.ace.config.v1.Environment> {

  private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
  private final uk.ac.ed.inf.ace.config.v1.DatabaseBase databaseConfig;
  private final Class<? extends Database> databaseType;
  private final HardCache hardCache = new HardCache("Environment");
  private final SupplierEx<File> outputDirectorySupplier =
      new SupplierEx<File>() {

        @Override
        public File get() throws Exception {
          return new File(getConfig().getOutputPath());
        }
      };
  private final SupplierEx<File> resultsFileSupplier =
      new SupplierEx<File>() {

        @Override
        public File get() throws Exception {
          return new File(getOutputDirectory(), "Results.csv");
        }
      };
  private final SupplierEx<DocType> classificationDocTypeSupplier =
      new SupplierEx<DocType>() {

        @Override
        public DocType get() throws Exception {
          for (DocType docType : getEngine().getDocTypes()) {
            if (docType.getName().equalsIgnoreCase(getConfig().getClassificationDocType())) {
              return docType;
            }
          }

          return null;
        }
      };
  private final ThreadLocal<Database> database = new ThreadLocal<Database>() {

    @Override
    protected Database initialValue() {
      try {
        E engine = getEngine();
        Database database = Utilities.construct(databaseType,
            new Class[]{engine.getClass(), databaseConfig.getClass()},
            new Object[]{engine, databaseConfig});
        return database;
      } catch (Exception exception) {
        throw new RuntimeException("Failed to open a thread-local database connection", exception);
      }
    }

    @Override
    public void remove() {
      try {
        get().close();
      } catch (Exception exception) {
        throw new RuntimeException("Failed to close a thread-local database connection", exception);
      }
    }
  };

  @SuppressWarnings("unchecked")
  private Environment(E engine, uk.ac.ed.inf.ace.config.v1.Environment config,
      uk.ac.ed.inf.ace.config.v1.DatabaseBase databaseConfig) throws Exception {
    super(engine, config);
    this.databaseConfig = databaseConfig;
    this.databaseType = (Class<? extends Database>) Class.forName(databaseConfig.getType());
  }

  public double getHoldOutProportion() {
    return getConfig().getHoldOutProportion().doubleValue();
  }

  public int getCacheCapacity() {
    return getConfig().getCacheCapacity();
  }

  public int getCacheEvictAfterAccess() {
    return getConfig().getCacheEvictAfterAccess();
  }

  public long getRandomSeed() {
    return getConfig().getRandomSeed();
  }

  public File getOutputDirectory() throws Exception {
    return hardCache.get(outputDirectorySupplier);
  }
  
  public File getResultsFile() throws Exception {
    return hardCache.get(resultsFileSupplier);
  }

  public int getThreadPoolSize() {
    return getConfig().getThreadPoolSize();
  }

  public Database getDatabase() {
    return database.get();
  }

  public int getCrossValidationFolds() {
    return getConfig().getCrossValidationFolds();
  }

  public DocType getClassificationDocType() throws Exception {
    return hardCache.get(classificationDocTypeSupplier);
  }

  public double getSampleProbability() {
    return getConfig().getSampleProbability().doubleValue();
  }

  private static uk.ac.ed.inf.ace.config.v1.DatabaseBase findDatabaseConfig(
      String databaseConfigName,
      Iterable<uk.ac.ed.inf.ace.config.v1.DatabaseBase> databaseConfigs) throws Exception {
    for (uk.ac.ed.inf.ace.config.v1.DatabaseBase databaseConfig : databaseConfigs) {
      if (databaseConfig.getName().equalsIgnoreCase(databaseConfigName)) {
        LOGGER.log(Level.INFO, "Accessing database {0}", databaseConfigName);
        return databaseConfig;
      }
    }

    throw new Exception("Failed to find the database " + databaseConfigName);
  }

  public static <E extends Engine<?, ?>> Environment<E> getActiveEnvironment(
      E engine, String name, Iterable<uk.ac.ed.inf.ace.config.v1.Environment> environmentConfigs,
      Iterable<uk.ac.ed.inf.ace.config.v1.DatabaseBase> databaseConfigs) throws Exception {
    Environment<E> environment = null;

    if (name == null) {
      for (uk.ac.ed.inf.ace.config.v1.Environment environmentConfig : environmentConfigs) {
        if (new File(environmentConfig.getOutputPath()).exists()) {
          environment = new Environment<>(engine, environmentConfig,
              findDatabaseConfig(environmentConfig.getDatabase(), databaseConfigs));
          break;
        }
      }

      Preconditions.checkNotNull(environment, "Unable to determine the active environment");
    } else {
      for (uk.ac.ed.inf.ace.config.v1.Environment environmentConfig : environmentConfigs) {
        if (environmentConfig.getName().equalsIgnoreCase(name)) {
          environment = new Environment<>(engine, environmentConfig,
              findDatabaseConfig(environmentConfig.getDatabase(), databaseConfigs));
          break;
        }
      }

      Preconditions.checkNotNull(environment, "Unable to locate named environment: {0}", name);
    }

    return environment;
  }

  @Override
  public boolean isValid() throws Exception {
    return super.isValid() && getClassificationDocType() != null;
  }
}