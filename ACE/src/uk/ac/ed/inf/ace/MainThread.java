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

import com.google.common.collect.Iterables;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.utils.StoppingException;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class MainThread extends Thread {

  private static final Logger LOGGER = Logger.getLogger(MainThread.class.getName());
  private final Engine<?, ?> engine;
  private final boolean rebuildDatabase;
  private final boolean generateTestData;
  private final boolean databaseQueryMode;
  private final boolean analysisOnly;

  public MainThread(Engine<?, ?> engine, boolean rebuildDatabase, boolean generateTestData,
      boolean databaseQueryMode, boolean analysisOnly) {
    this.engine = engine;
    this.rebuildDatabase = rebuildDatabase;
    this.generateTestData = generateTestData;
    this.databaseQueryMode = databaseQueryMode;
    this.analysisOnly = analysisOnly;
  }

  private static void stopped() {
    LOGGER.info("Main thread stopped early");
  }

  private void exception(Exception exception) {
    LOGGER.log(Level.SEVERE, "Main thread failed", exception);

    try {
      engine.stop();
    } catch (Exception innerException) {
      LOGGER.log(Level.SEVERE, "Main thread failed to stop engine", innerException);
    }

    System.exit(-1);
  }

  @Override
  public void run() {
    try {
      RandomSource randomSource = new RandomSource(engine.getEnvironment().getRandomSeed());

      try (Database initialDatabase = engine.getEnvironment().getDatabase()) {
        initialDatabase.open(randomSource, rebuildDatabase, generateTestData);
      }

      if (databaseQueryMode) {
        return;
      }

      // Use a completion service to wait for all programmes to complete
      Iterable<Programme> programmes = engine.getProgrammes();
      int programmeCount = Iterables.size(programmes);

      if (programmeCount == 0) {
        LOGGER.warning("There are no valid programmes to run.");
      } else {
        CompletionService<Boolean> completionService =
            new ExecutorCompletionService<>(engine.getThreadPool());
        int queuedTasks = 0;

        for (Programme programme : programmes) {
          completionService.submit(programme.getExecutable(randomSource, analysisOnly));
          queuedTasks++;
        }

        for (int index = 0; index < queuedTasks; index++) {
          completionService.take().get();
        }

        engine.getThreadPool().stop();
        LOGGER.info("All programmes completed");
      }
    } catch (StoppingException exception) {
      stopped();
    } catch (ExecutionException exception) {
      Throwable temp = exception;

      while (true) {
        if (temp == null) {
          break;
        }

        if (temp instanceof StoppingException) {
          stopped();
          return;
        }

        temp = temp.getCause();
      }

      exception(exception);
    } catch (Exception exception) {
      exception(exception);
    }
  }
}