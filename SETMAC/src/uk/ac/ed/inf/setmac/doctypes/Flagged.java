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
package uk.ac.ed.inf.setmac.doctypes;

import java.sql.ResultSet;
import uk.ac.ed.inf.ace.DocTypeBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.setmac.Engine;
import uk.ac.ed.inf.setmac.databases.DB;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Flagged extends DocTypeBase<Engine, uk.ac.ed.inf.setmac.config.v1.Flagged> {

  public Flagged(Engine engine, uk.ac.ed.inf.setmac.config.v1.Flagged config) {
    super(engine, config);
  }

  private static void addContent(ResultSet resultSet, StringBuilder content,
      int columnIndex) throws Exception {
    String text = resultSet.getString(columnIndex);

    if (!resultSet.wasNull() && text != null) {
      content.append(" ");
      content.append(text);
    }
  }

  @Override
  public ReadWriteableDocument getDocument(String documentId) throws Exception {
    ReadWriteableDocument document;

    try (ResultSet resultSet = DB.getDocumentDetails(getEngine().getEnvironment().getDatabase(),
            documentId)) {
      uk.ac.ed.inf.setmac.config.v1.Flagged config = getConfig();
      StringBuilder content = new StringBuilder();
      int state = -1;

      while (resultSet.next()) {
        int rowType = resultSet.getInt(1);

        if (rowType == 1) {
          state = resultSet.getInt(5);
        } else if ((rowType == 2 && !config.isIncludeAnswers())
            || (rowType == 3 && !config.isIncludeQuestionComments())
            || (rowType == 4 && !config.isIncludeAnswerComments())) {
          continue;
        }

        addContent(resultSet, content, 2);
        addContent(resultSet, content, 3);
        addContent(resultSet, content, 4);
      }

      document = new ReadWriteableDocument(documentId, content.toString());
      document.getProperties().put(getConfig().getLabelPropertyKey(), state);
    }

    return document;
  }
}