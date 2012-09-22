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
package uk.ac.ed.inf.ace.utils;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ed.inf.ace.Database;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class PreparedStatements extends InstrumentedCache<Supplier<String>, PreparedStatement> {

  private static final Logger LOGGER = Logger.getLogger(PreparedStatements.class.getName());

  public PreparedStatements(final Database database) {
    super("PreparedStatements",
        CacheBuilder.newBuilder().removalListener(
        new RemovalListener<Supplier<String>, PreparedStatement>() {
          @Override
          public void onRemoval(
              RemovalNotification<Supplier<String>, PreparedStatement> notification) {
            try {
              notification.getValue().close();
            } catch (SQLException exception) {
              LOGGER.log(Level.SEVERE, "Failed to close a prepared statement.", exception);
            }
          }
        }).concurrencyLevel(1).build(
        new CacheLoader<Supplier<String>, PreparedStatement>() {
          @Override
          public PreparedStatement load(Supplier<String> sqlSupplier) throws Exception {
            database.open();
            return database.prepareStatement(sqlSupplier.get());
          }
        }));
  }

  @Override
  public PreparedStatement get(Supplier<String> query) throws Exception {
    return super.get(query);
  }

  @Override
  public PreparedStatement getUnchecked(Supplier<String> query) {
    return super.getUnchecked(query);
  }
}