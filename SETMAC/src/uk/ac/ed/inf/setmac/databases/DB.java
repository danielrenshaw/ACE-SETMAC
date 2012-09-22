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

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import uk.ac.ed.inf.ace.Database;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DB {

  private static class GetDocumentIdsSqlSupplier implements Supplier<String> {

    private final int siteIdsCount;

    private GetDocumentIdsSqlSupplier(int siteIdsCount) {
      this.siteIdsCount = siteIdsCount;
    }

    @Override
    public String get() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(" SELECT");
      stringBuilder.append("     Question.SiteId || \"-\" || Question.Id AS DocumentId");
      stringBuilder.append(" FROM Post AS Question");
      stringBuilder.append(" WHERE Question.PostTypeId = 1");
      stringBuilder.append("     AND Question.IsTest = ?");
      stringBuilder.append("     AND Question.SiteId IN (");

      for (int index = 0; index < siteIdsCount; index++) {
        stringBuilder.append("?");
        stringBuilder.append(", ");
      }

      stringBuilder.deleteCharAt(stringBuilder.length() - 2);
      stringBuilder.append("     )");
      stringBuilder.append(" ORDER BY");
      stringBuilder.append("     Question.SiteId,");
      stringBuilder.append("     Question.Id");
      return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }

      if (getClass() != obj.getClass()) {
        return false;
      }

      GetDocumentIdsSqlSupplier other = (GetDocumentIdsSqlSupplier) obj;
      return this.siteIdsCount == other.siteIdsCount;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(siteIdsCount);
    }
  }
  private static final Supplier<String> selectSiteIdSqlSupplier = new Supplier<String>() {
    @Override
    public String get() {
      return "SELECT Id FROM Site WHERE Name = ?";
    }
  };
  private static final Supplier<String> insertSiteSqlSupplier = new Supplier<String>() {
    @Override
    public String get() {
      return "INSERT INTO Site VALUES (NULL, ?)";
    }
  };
  private static final Supplier<String> getDocumentDetailsSqlSupplier = new Supplier<String>() {
    @Override
    public String get() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(" SELECT");
      stringBuilder.append("     1 AS RowType,");
      stringBuilder.append("     Question.Body,");
      stringBuilder.append("     Question.Title,");
      stringBuilder.append("     Question.Tags,");
      stringBuilder.append("     IFNULL((");
      stringBuilder.append("         SELECT");
      stringBuilder.append("             CASE PostHistory.PostHistoryTypeId");
      stringBuilder.append("                 WHEN 10 THEN");
      stringBuilder.append("                     CASE PostHistory.CloseReasonId");
      stringBuilder.append("                         WHEN 2 THEN 1"); // Off topic
      stringBuilder.append("                         WHEN 3 THEN 2"); // Not constructive
      stringBuilder.append("                         WHEN 4 THEN 3"); // Not a real question
      stringBuilder.append("                         WHEN 7 THEN 4"); // Too localized
      stringBuilder.append("                         ELSE -1"); // This should never happen
      stringBuilder.append("                     END");
      stringBuilder.append("                 WHEN 11 THEN 0");
      stringBuilder.append("                 ELSE -1"); // This should never happen
      stringBuilder.append("             END");
      stringBuilder.append("         FROM PostHistory");
      stringBuilder.append("         WHERE PostHistory.PostId = Question.Id");
      stringBuilder.append("             AND PostHistory.SiteId = Question.SiteId");
      stringBuilder.append("             AND PostHistory.PostHistoryTypeId BETWEEN 10 AND 11");
      stringBuilder.append("             AND");
      stringBuilder.append("             (");
      stringBuilder.append("                 PostHistory.CloseReasonId IS NULL");
      stringBuilder.append("                     OR PostHistory.CloseReasonId IN (2, 3, 4, 7)");
      stringBuilder.append("             )");
      stringBuilder.append("         ORDER BY");
      stringBuilder.append("             PostHistory.CreationDate DESC,");
      stringBuilder.append("             PostHistory.Id");
      stringBuilder.append("         LIMIT 1");
      stringBuilder.append("     ), 0) AS State");
      stringBuilder.append(" FROM Post AS Question");
      stringBuilder.append(" WHERE Question.Id = ?");
      stringBuilder.append("     AND Question.SiteId = ?");
      stringBuilder.append(" UNION ALL");
      stringBuilder.append(" SELECT");
      stringBuilder.append("     2,");
      stringBuilder.append("     Answer.Body,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL");
      stringBuilder.append(" FROM Post AS Answer");
      stringBuilder.append(" WHERE Answer.ParentId = ?");
      stringBuilder.append("     AND Answer.SiteId = ?");
      stringBuilder.append("     AND Answer.PostTypeId = 2");
      stringBuilder.append("     AND Answer.ClosedDate IS NULL");
      stringBuilder.append(" UNION ALL");
      stringBuilder.append(" SELECT");
      stringBuilder.append("     3,");
      stringBuilder.append("     QuestionComment.Text,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL");
      stringBuilder.append(" FROM Comment AS QuestionComment");
      stringBuilder.append(" WHERE QuestionComment.PostId = ?");
      stringBuilder.append("     AND QuestionComment.SiteId = ?");
      stringBuilder.append(" UNION ALL");
      stringBuilder.append(" SELECT");
      stringBuilder.append("     4,");
      stringBuilder.append("     AnswerComment.Text,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL,");
      stringBuilder.append("     NULL");
      stringBuilder.append(" FROM Post AS Answer");
      stringBuilder.append("     INNER JOIN Comment AS AnswerComment");
      stringBuilder.append("         ON AnswerComment.PostId = Answer.Id");
      stringBuilder.append("             AND AnswerComment.SiteId = Answer.SiteId");
      stringBuilder.append(" WHERE Answer.ParentId = ?");
      stringBuilder.append("     AND Answer.SiteId = ?");
      stringBuilder.append("     AND Answer.PostTypeId = 2");
      stringBuilder.append("     AND Answer.ClosedDate IS NULL");
      return stringBuilder.toString();
    }
  };

  private DB() {
  }

  public static int getSiteId(Database database, String name) throws Exception {
    PreparedStatement selectSiteId = database.getPreparedStatements().get(selectSiteIdSqlSupplier);
    selectSiteId.setString(1, name);

    try (ResultSet resultSet = selectSiteId.executeQuery()) {
      if (resultSet.next()) {
        return resultSet.getInt("Id");
      } else {
        PreparedStatement statement = database.getPreparedStatements().get(insertSiteSqlSupplier);
        statement.setString(1, name);
        statement.executeUpdate();
        return getSiteId(database, name);
      }
    }
  }

  public static ResultSet getDocumentIds(Database database, boolean forTest, Set<Integer> siteIds)
      throws Exception {
    int size = siteIds.size();

    PreparedStatement statement = database.getPreparedStatements().get(
        new GetDocumentIdsSqlSupplier(size));
    statement.setBoolean(1, forTest);
    size += 1;

    for (Integer siteId : siteIds) {
      statement.setInt(size--, siteId);
    }

    return statement.executeQuery();
  }

  public static ResultSet getDocumentDetails(Database database, String documentId)
      throws Exception {
    String[] parts = documentId.split("-");
    int siteId = Integer.parseInt(parts[0]);
    int questionId = Integer.parseInt(parts[1]);
    PreparedStatement statement = database.getPreparedStatements().get(
        getDocumentDetailsSqlSupplier);

    for (int index = 1; index <= 8; index += 2) {
      statement.setInt(index, questionId);
      statement.setInt(index + 1, siteId);
    }

    return statement.executeQuery();
  }
}