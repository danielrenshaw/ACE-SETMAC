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
package uk.ac.ed.inf.setmac.databases;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.databases.SqliteDatabaseBase;
import uk.ac.ed.inf.ace.utils.Utilities;
import uk.ac.ed.inf.setmac.Engine;
import uk.ac.ed.inf.setmac.Site;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class SqliteDatabase extends SqliteDatabaseBase<Engine, uk.ac.ed.inf.setmac.config.v1.SqliteDatabase> {

  private static final Logger LOGGER = Logger.getLogger(SqliteDatabase.class.getName());
  private static final String DEFAULT_PATH = "setmac.db";
  private static final String DDL_NAME = "ddl.sql";
  private File xmlDirectory;

  public SqliteDatabase(Engine engine, uk.ac.ed.inf.setmac.config.v1.SqliteDatabase config)
      throws Exception {
    super(engine, config, new File(Utilities.ifNull(config.getPathname(), DEFAULT_PATH)),
        new File(SqliteDatabase.class.getResource(DDL_NAME).getFile()));
  }

  private File getXmlFile(Site site, String pathname) throws Exception {
    if (xmlDirectory == null) {
      xmlDirectory = new File(getConfig().getXmlPath());
    }

    return new File(xmlDirectory, site.getXmlFile(pathname).getPath());
  }

  @Override
  protected void loadDataSets(long randomSeed, boolean generateTestData) throws Exception {
    Random random = new Random(randomSeed);

    if (generateTestData) {
      Generator generator = new Generator(random.nextLong(), 1_000, new String[]{"aaa", "bbb",
            "ccc", "ddd", "eee"}, new double[][]{new double[]{1.0, 0.0, 0.0, 0.0, 0.0},
            new double[]{0.0, 0.0, 1.0, 0.0, 0.0}, new double[]{0.0, 0.0, 0.0, 0.0, 1.0}},
          new double[][]{new double[]{1.0, 0.0, 0.0, 0.0, 0.0},
            new double[]{1.0, 0.0, 0.0, 0.0, 0.0}, new double[]{0.0, 1.0, 0.0, 0.0, 0.0}}, 3, 2, 7,
          0.01);

      for (Site site : getEngine().getSites()) {
        if (site.getName().equals("Test")) {
          generator.write(getXmlFile(site, ""));
          break;
        }
      }
    }

    Engine engine = getEngine();
    XmlImporter xmlImporter = new XmlImporter(engine);

    for (Site site : getEngine().getSites()) {
      if (engine.isStopping()) {
        return;
      }

      randomSeed = random.nextLong();
      String siteName = site.getName();
      LOGGER.log(Level.INFO, "Loading data for {0}", siteName);
      xmlImporter.importXml(getXmlFile(site, "posts.xml"), siteName, randomSeed);
      xmlImporter.importXml(getXmlFile(site, "posthistory.xml"), siteName, randomSeed);
      xmlImporter.importXml(getXmlFile(site, "comments.xml"), siteName, randomSeed);
    }
  }

  @Override
  protected void optimize(Connection connection) throws Exception {
    Statement statement = connection.createStatement();

    // Covering index to improve performance of the SiteBasedDataSet
    LOGGER.log(Level.INFO, "Adding PostOpt1 index");
    statement.executeUpdate("CREATE UNIQUE INDEX PostOpt1 ON Post (PostTypeId, IsTest, SiteId,"
        + " Id)");

    // Covering index to improve performance of the Flagged DocType when getting answers
    LOGGER.log(Level.INFO, "Adding PostOpt2 index");
    statement.executeUpdate("CREATE INDEX PostOpt2 ON Post (ParentId, SiteId, PostTypeId,"
        + " ClosedDate, Body)");

    // Covering index to improve performance of the Flagged DocType when getting question state
    LOGGER.log(Level.INFO, "Adding PostHistoryOpt1 index");
    statement.executeUpdate("CREATE UNIQUE INDEX PostHistoryOpt1 ON PostHistory (PostId, SiteId,"
        + " CreationDate DESC, Id, PostHistoryTypeId, CloseReasonId)");

    // Covering index to improve performance of the Flagged DocType when getting comments
    LOGGER.log(Level.INFO, "Adding CommentOpt1 index");
    statement.executeUpdate("CREATE INDEX CommentOpt1 ON Comment (SiteId, PostId, Text)");

    // Rebuild the statistics to improve query optimization
    LOGGER.log(Level.INFO, "Updating statistics");
    statement.executeUpdate("ANALYZE");

    // Rebuild the database to remove fragmentation and release any wasted space
    LOGGER.log(Level.INFO, "Defragmenting and releasing wasted space");
    statement.executeUpdate("VACUUM");
  }
}