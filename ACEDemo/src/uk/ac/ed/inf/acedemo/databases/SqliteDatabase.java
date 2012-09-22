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
package uk.ac.ed.inf.acedemo.databases;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import uk.ac.ed.inf.ace.SimpleEngine;
import uk.ac.ed.inf.ace.databases.SqliteDatabaseBase;
import uk.ac.ed.inf.ace.utils.Utilities;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class SqliteDatabase extends SqliteDatabaseBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.SqliteDatabase> {

  private static final String DEFAULT_PATH = "acedemo.db";
  private static final String DDL_NAME = "ddl.sql";
  private static final String DATA_NAME = "spambase.data";

  public SqliteDatabase(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.SqliteDatabase config)
      throws Exception {
    super(engine, config,
        new File(Utilities.ifNull(config.getPathname(), DEFAULT_PATH)),
        new File(SqliteDatabase.class.getResource(DDL_NAME).getFile()));
  }

  @Override
  protected void loadDataSets(long randomSeed, boolean generateTestData) throws Exception {
    StringBuilder stringBuilder = new StringBuilder("BEGIN;\n");

    try (InputStream inputStream = SqliteDatabase.class.getResourceAsStream(DATA_NAME)) {
      try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
          String line;

          while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();

            if (line.isEmpty()) {
              continue;
            }

            stringBuilder.append("INSERT INTO Item VALUES (");
            stringBuilder.append(line);
            stringBuilder.append(");\n");
          }
        }
      }
    }

    stringBuilder.append("COMMIT;\n");
    createStatement().executeUpdate(stringBuilder.toString());
  }
}