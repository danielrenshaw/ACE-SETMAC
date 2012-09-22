/*
 * Copyright 2012 Daniel.
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
package uk.ac.ed.inf.acedemo.datasets;

import com.google.common.base.Supplier;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Random;
import uk.ac.ed.inf.ace.DataSetBase;
import uk.ac.ed.inf.ace.SimpleEngine;
import uk.ac.ed.inf.ace.utils.ScalarResultSetIterator;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DataSet extends DataSetBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.DataSet> {

  private static final Supplier<String> getDocumentIdsSqlSupplier = new Supplier<String>() {
    @Override
    public String get() {
      return " SELECT CAST(rowid AS TEXT) AS id FROM Item";
    }
  };

  public DataSet(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.DataSet config) {
    super(engine, config);
  }

  @Override
  protected Iterator<String> getDocumentIds(boolean forTest, Random random) throws Exception {
    PreparedStatement statement = getEngine().getEnvironment().getDatabase().getPreparedStatements()
        .get(getDocumentIdsSqlSupplier);
    return new ScalarResultSetIterator<>(statement.executeQuery());
  }
}
