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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import uk.ac.ed.inf.ace.cli.CommandInterface;
import uk.ac.ed.inf.ace.cli.OutStream;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Ace {

  private static final Logger LOGGER = Logger.getLogger(Ace.class.getName());
  private static final String CONFIG_PATH_OPTION = "c";
  private static final String ENVIRONMENT_NAME_OPTION = "e";
  private static final String EXTENSION_SCHEMA_PATH_OPTION = "s";
  private static final String EXTENSION_CONTEXT_PATH_OPTION = "x";
  private static final String REBUILD_DATABASE_OPTION = "r";
  private static final String DATABASE_QUERY_MODE_OPTION = "d";
  private static final String PURGE_OUTPUT_OPTION = "p";
  private static final String ANALYSIS_ONLY_OPTION = "a";
  private static final String GENERATE_TEST_DATA_OPTION = "g";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    try {
      OutStream outStream = new OutStream(System.out);
      System.setOut(outStream);
      System.setErr(outStream);
      LOGGER.info("Starting");
      CommandLine commandLine = getCommandLine(args);

      Engine<?, ?> engine = null;
      InputStreamReader inputStreamReader = null;
      BufferedReader bufferedReader = null;

      try {
        // Load the configuration
        engine = Config.load(commandLine.getOptionValue(CONFIG_PATH_OPTION),
            commandLine.getOptionValue(ENVIRONMENT_NAME_OPTION),
            commandLine.getOptionValue(EXTENSION_SCHEMA_PATH_OPTION),
            commandLine.getOptionValue(EXTENSION_CONTEXT_PATH_OPTION));

        // Set up the output directory
        File file = engine.getEnvironment().getOutputDirectory();

        if (commandLine.hasOption(PURGE_OUTPUT_OPTION)) {
          File resultsFile = engine.getEnvironment().getResultsFile();

          synchronized (resultsFile) {
            while (resultsFile.exists()) {
              resultsFile.delete();
            }
          }

          if (!commandLine.hasOption(ANALYSIS_ONLY_OPTION)) {
            while (file.exists()) {
              FileUtils.deleteQuietly(file);
            }
          }
        }

        while (!file.exists()) {
          file.mkdirs();
        }

        // Start the engine (it runs on its own thread)
        engine.start(commandLine.hasOption(REBUILD_DATABASE_OPTION),
            commandLine.hasOption(GENERATE_TEST_DATA_OPTION),
            commandLine.hasOption(DATABASE_QUERY_MODE_OPTION),
            commandLine.hasOption(ANALYSIS_ONLY_OPTION));

        // Set up a basic REPL
        inputStreamReader = new InputStreamReader(System.in);
        bufferedReader = new BufferedReader(inputStreamReader);

        CommandInterface commandInterface = new CommandInterface(engine);
        outStream.printPrompt("> ");
        String command;

        while ((command = bufferedReader.readLine()) != null) {
          if (commandInterface.execute(command)) {
            break;
          }
        }

        LOGGER.info("Quitting");
      } finally {
        if (engine != null) {
          engine.stop();
        }

        if (inputStreamReader != null) {
          inputStreamReader.close();
        }

        if (bufferedReader != null) {
          bufferedReader.close();
        }
      }
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, "System thread failed", exception);
    }
  }

  private static CommandLine getCommandLine(String[] args)
      throws Exception {
    // Define the command line options
    Options options = new Options();
    options.addOption(CONFIG_PATH_OPTION, true, "path to a config XML file");
    options.addOption(ENVIRONMENT_NAME_OPTION, true,
        "if set, will force use of the specified environment");
    options.addOption(EXTENSION_SCHEMA_PATH_OPTION, true,
        "if set, will validate the config extensions according to the specified schema");
    options.addOption(EXTENSION_CONTEXT_PATH_OPTION, true,
        "if set, will extend the JAXB context path as required");
    options.addOption(REBUILD_DATABASE_OPTION, false,
        "if set, will force the database to be rebuilt");
    options.addOption(DATABASE_QUERY_MODE_OPTION, false,
        "if set, will stop experiments being run but will prepare the database ready to be "
        + "queried");
    options.addOption(PURGE_OUTPUT_OPTION, false,
        "if set, will delete the output directory before running any experiments");
    options.addOption(ANALYSIS_ONLY_OPTION, false,
        "if set, will not run any experiments but will skip straight to analysing the results");
    options.addOption(GENERATE_TEST_DATA_OPTION, false,
        "if set, will first generate a new set of test data");

    // Parse the command line
    CommandLineParser commandLineParser = new GnuParser();
    CommandLine commandLine = commandLineParser.parse(options, args);
    return commandLine;
  }
}