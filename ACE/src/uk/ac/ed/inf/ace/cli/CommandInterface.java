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
package uk.ac.ed.inf.ace.cli;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class CommandInterface {

  private static final Logger LOGGER = Logger.getLogger(CommandInterface.class.getName());
  private final Engine<?, ?> engine;

  public CommandInterface(Engine<?, ?> engine) {
    this.engine = engine;
  }

  public boolean execute(String command) {
    if (command.equalsIgnoreCase("q")) {
      return true;
    }

    try {
      if (command.equalsIgnoreCase("id")) {
        engine.getDocumentCache().invalidateAll();
        LOGGER.info("Document cache invalidated");
      } else if (command.toLowerCase().startsWith("d ")) {
        long start = System.currentTimeMillis();
        Statement statement = engine.getEnvironment().getDatabase().createStatement();

        if (statement.execute(command.substring(2))) {
          do {
            ResultSet resultSet = statement.getResultSet();

            if (resultSet == null) {
              LOGGER.log(Level.INFO, "Update count: {0}", statement.getUpdateCount());
            } else {
              ResultSetMetaData metaData = resultSet.getMetaData();
              StringBuilder stringBuilder = new StringBuilder();

              for (int index = 1; index <= metaData.getColumnCount(); index++) {
                stringBuilder.append(metaData.getColumnName(index));
                stringBuilder.append(",");
              }

              LOGGER.info(stringBuilder.substring(0, stringBuilder.length() - 1));

              while (resultSet.next()) {
                stringBuilder.setLength(0);

                for (int index = 1; index <= metaData.getColumnCount(); index++) {
                  stringBuilder.append(resultSet.getObject(index));
                  stringBuilder.append(",");
                }

                LOGGER.info(stringBuilder.substring(0, stringBuilder.length() - 1).replace("'",
                    "''").replace("{", "'{'"));
              }
            }
          } while (statement.getMoreResults() || statement.getUpdateCount() != -1);
        }

        LOGGER.log(Level.INFO, "Time: {0}", System.currentTimeMillis() - start);
      } else {
        LOGGER.log(Level.WARNING, "Unknown command: {0}", command);
      }
    } catch (Exception exception) {
      LOGGER.log(Level.WARNING, "Command caused an exception", exception);
    }

    return false;
  }
}