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
package uk.ac.ed.inf.ace.databases;

import com.google.common.base.Preconditions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteConfig;
import uk.ac.ed.inf.ace.DatabaseBase;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.utils.InflaterPreparedStatement;
import uk.ac.ed.inf.ace.utils.InflaterStatement;
import uk.ac.ed.inf.ace.utils.PreparedStatements;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class SqliteDatabaseBase<E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.SqliteDatabaseBase> extends DatabaseBase<E, C> {

  private static final Logger LOGGER = Logger.getLogger(SqliteDatabaseBase.class.getName());
  private final File dbFile;
  private final File ddlFile;
  private final boolean defaultReadOnly;
  private final PreparedStatements preparedStatements;
  private Connection connection;

  public SqliteDatabaseBase(E engine, C config, File dbFile, File ddlFile)
      throws Exception {
    super(engine, config);
    this.dbFile = dbFile;
    this.ddlFile = ddlFile;
    this.defaultReadOnly = config.isReadOnly();
    this.preparedStatements = new PreparedStatements(this);
  }

  @Override
  public void close() throws Exception {
    if (connection != null) {
      LOGGER.info("Closing database");
      preparedStatements.invalidateAll();
      connection.close();
      connection = null;
    }
  }

  @Override
  public void open() throws Exception {
    open(defaultReadOnly);
  }

  public void open(boolean readOnly) throws Exception {
    if (connection == null) {
      LOGGER.info("Opening database");
      Class.forName("org.sqlite.JDBC");
      SQLiteConfig config = new SQLiteConfig();
      config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
      config.setReadOnly(readOnly);
      config.setSharedCache(true);
      config.setCacheSize(getConfig().getCacheSize());
      connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile, config.toProperties());
    }
  }

  private void build() throws Exception {
    LOGGER.log(Level.INFO, "Creating database structure");
    Statement statement = connection.createStatement();
    FileInputStream fileInputStream = null;
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;

    try {
      fileInputStream = new FileInputStream(ddlFile);
      inputStreamReader = new InputStreamReader(fileInputStream,
          uk.ac.ed.inf.ace.utils.Constants.UTF8_CHARSET);
      bufferedReader = new BufferedReader(inputStreamReader);

      StringBuilder stringBuilder = new StringBuilder();
      String line;

      while ((line = bufferedReader.readLine()) != null) {
        if (line.equalsIgnoreCase("GO")) {
          String sql = stringBuilder.toString().trim();

          if (!sql.isEmpty()) {
            statement.executeUpdate(stringBuilder.toString());
          }

          stringBuilder.setLength(0);
        } else if (!line.trim().startsWith("--")) {
          stringBuilder.append(line);
        }
      }
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }

      if (inputStreamReader != null) {
        inputStreamReader.close();
      }

      if (fileInputStream != null) {
        fileInputStream.close();
      }
    }
  }

  protected void optimize(Connection connection) throws Exception {
  }

  protected abstract void loadDataSets(long randomSeed, boolean generateTestData) throws Exception;

  @Override
  public void open(RandomSource randomSource, boolean rebuild, boolean generateTestData)
      throws Exception {
    boolean exists = dbFile.exists();
    Random random = randomSource.get(this);

    if (rebuild) {
      LOGGER.log(Level.INFO, "Database rebuild requested");

      if (exists) {
        LOGGER.log(Level.INFO, "Database already exists, deleting");
        close();
        dbFile.delete();
      }

      open(false);
      build();
      loadDataSets(random.nextLong(), generateTestData);
      optimize(connection);
      LOGGER.log(Level.INFO, "Database build complete");
    } else {
      Preconditions.checkState(exists, "Database missing and rebuild not requested");
      open(defaultReadOnly);
    }
  }

  @Override
  public Statement createStatement() throws Exception {
    open();
    InflaterStatement statement = new InflaterStatement(connection.createStatement(
        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT));
    return statement;
  }

  @Override
  public PreparedStatement prepareStatement(String sql) throws Exception {
    open();
    InflaterPreparedStatement statement = new InflaterPreparedStatement(connection.prepareStatement(
        sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        ResultSet.CLOSE_CURSORS_AT_COMMIT));
    return statement;
  }

  @Override
  public PreparedStatements getPreparedStatements() {
    return preparedStatements;
  }
}