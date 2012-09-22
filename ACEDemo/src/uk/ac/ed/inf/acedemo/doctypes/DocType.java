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
package uk.ac.ed.inf.acedemo.doctypes;

import com.google.common.base.Supplier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import uk.ac.ed.inf.ace.DocTypeBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.SimpleEngine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DocType extends DocTypeBase<SimpleEngine, uk.ac.ed.inf.ace.config.v1.DocType> {

  private static final Supplier<String> getDocumentDetailsSqlSupplier = new Supplier<String>() {
    @Override
    public String get() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(" SELECT");
      stringBuilder.append("     word_freq_make,");
      stringBuilder.append("     word_freq_address,");
      stringBuilder.append("     word_freq_all,");
      stringBuilder.append("     word_freq_3d,");
      stringBuilder.append("     word_freq_our,");
      stringBuilder.append("     word_freq_over,");
      stringBuilder.append("     word_freq_remove,");
      stringBuilder.append("     word_freq_internet,");
      stringBuilder.append("     word_freq_order,");
      stringBuilder.append("     word_freq_mail,");
      stringBuilder.append("     word_freq_receive,");
      stringBuilder.append("     word_freq_will,");
      stringBuilder.append("     word_freq_people,");
      stringBuilder.append("     word_freq_report,");
      stringBuilder.append("     word_freq_addresses,");
      stringBuilder.append("     word_freq_free,");
      stringBuilder.append("     word_freq_business,");
      stringBuilder.append("     word_freq_email,");
      stringBuilder.append("     word_freq_you,");
      stringBuilder.append("     word_freq_credit,");
      stringBuilder.append("     word_freq_your,");
      stringBuilder.append("     word_freq_font,");
      stringBuilder.append("     word_freq_000,");
      stringBuilder.append("     word_freq_money,");
      stringBuilder.append("     word_freq_hp,");
      stringBuilder.append("     word_freq_hpl,");
      stringBuilder.append("     word_freq_george,");
      stringBuilder.append("     word_freq_650,");
      stringBuilder.append("     word_freq_lab,");
      stringBuilder.append("     word_freq_labs,");
      stringBuilder.append("     word_freq_telnet,");
      stringBuilder.append("     word_freq_857,");
      stringBuilder.append("     word_freq_data,");
      stringBuilder.append("     word_freq_415,");
      stringBuilder.append("     word_freq_85,");
      stringBuilder.append("     word_freq_technology,");
      stringBuilder.append("     word_freq_1999,");
      stringBuilder.append("     word_freq_parts,");
      stringBuilder.append("     word_freq_pm,");
      stringBuilder.append("     word_freq_direct,");
      stringBuilder.append("     word_freq_cs,");
      stringBuilder.append("     word_freq_meeting,");
      stringBuilder.append("     word_freq_original,");
      stringBuilder.append("     word_freq_project,");
      stringBuilder.append("     word_freq_re,");
      stringBuilder.append("     word_freq_edu,");
      stringBuilder.append("     word_freq_table,");
      stringBuilder.append("     word_freq_conference,");
      stringBuilder.append("     char_freq_sc,");
      stringBuilder.append("     char_freq_op,");
      stringBuilder.append("     char_freq_os,");
      stringBuilder.append("     char_freq_em,");
      stringBuilder.append("     char_freq_ds,");
      stringBuilder.append("     char_freq_hs,");
      stringBuilder.append("     capital_run_length_average,");
      stringBuilder.append("     capital_run_length_longest,");
      stringBuilder.append("     capital_run_length_total,");
      stringBuilder.append("     is_spam");
      stringBuilder.append(" FROM Item");
      stringBuilder.append(" WHERE rowid = ?");
      return stringBuilder.toString();
    }
  };

  public DocType(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.DocType config) {
    super(engine, config);
  }

  @Override
  public ReadWriteableDocument getDocument(String documentId) throws Exception {
    ReadWriteableDocument document;
    PreparedStatement statement = getEngine().getEnvironment().getDatabase().getPreparedStatements()
        .get(getDocumentDetailsSqlSupplier);
    statement.setLong(1, Long.parseLong(documentId));

    try (ResultSet resultSet = statement.executeQuery()) {
      double[] content = new double[57];

      for (int index = 0; index < content.length; index++) {
        content[index] = resultSet.getDouble(index + 1);
      }

      document = new ReadWriteableDocument(documentId, content);
      document.getProperties().put(getConfig().getLabelPropertyKey(), resultSet.getInt(58));
    }

    return document;
  }
}
