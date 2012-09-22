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

import java.util.Arrays;
import java.util.Random;
import java.util.zip.Deflater;
import org.apache.commons.codec.binary.Hex;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.ed.inf.ace.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class XmlHandler extends DefaultHandler {

  private static final int BATCH_SIZE = 10_000;
  private static final int DEFLATE_BUFFER_SIZE = 4_096;
  private static final byte[] DEFAULT_BUFFER = new byte[DEFLATE_BUFFER_SIZE];

  private static interface SqlGenerator {

    void apply(Attributes attributes) throws Exception;
  }
  private final Deflater deflater = new Deflater();

  private String getIfNotNull(Attributes attributes, String name) {
    String value = attributes.getValue(name);

    if (value != null) {
      value = value.trim();
    }

    if (value == null || value.isEmpty()) {
      stringBuilder.append("NULL,");
      return null;
    }

    return value;
  }

  private Integer addInteger(Attributes attributes, String name) {
    String value = getIfNotNull(attributes, name);

    if (value == null) {
      return null;
    }

    int parsedValue = Integer.parseInt(value);
    stringBuilder.append(value);
    stringBuilder.append(",");
    return parsedValue;
  }

  private void addText(Attributes attributes, String name) {
    addText(attributes, name, false);
  }

  private void addText(Attributes attributes, String name, boolean compress) {
    String value = getIfNotNull(attributes, name);

    if (value == null) {
      return;
    }

    if (compress) {
      deflater.reset();
      deflater.setInput(value.getBytes(uk.ac.ed.inf.ace.utils.Constants.UTF8_CHARSET));
      deflater.finish();
      stringBuilder.append("X'");
      byte[] buffer = DEFAULT_BUFFER;

      while (!deflater.finished()) {
        int bytesInBuffer = deflater.deflate(buffer);

        if (bytesInBuffer < DEFLATE_BUFFER_SIZE) {
          buffer = Arrays.copyOf(buffer, bytesInBuffer);
        }

        stringBuilder.append(Hex.encodeHex(buffer));
      }
    } else {
      stringBuilder.append("'");
      stringBuilder.append(value.replace("'", "''"));
    }

    stringBuilder.append("',");
  }

  private void addNumericDateTime(Attributes attributes, String name) {
    String value = getIfNotNull(attributes, name);

    if (value == null) {
      return;
    }

    stringBuilder.append("date('");
    stringBuilder.append(value);
    stringBuilder.append("'),");
  }
  private SqlGenerator generatePostSql = new SqlGenerator() {
    @Override
    public void apply(Attributes attributes) throws Exception {
      stringBuilder.append("INSERT INTO Post VALUES (");
      stringBuilder.append(siteId);
      stringBuilder.append(",");
      addInteger(attributes, "Id");
      int postTypeId = addInteger(attributes, "PostTypeId");
      addInteger(attributes, "ParentId");
      //addInteger(attributes, "AcceptedAnswerId");
      //addNumericDateTime(attributes, "CreationDate");
      //addInteger(attributes, "Score");
      //addInteger(attributes, "ViewCount");
      addText(attributes, "Body", true);
      //addInteger(attributes, "OwnerUserId");
      //addInteger(attributes, "LastEditorUserId");
      //addText(attributes, "LastEditorDisplayName");
      //addNumericDateTime(attributes, "LastEditDate");
      //addNumericDateTime(attributes, "LastActivityDate");
      //addNumericDateTime(attributes, "CommunityOwnedDate");
      addNumericDateTime(attributes, "ClosedDate");
      addText(attributes, "Title");
      addText(attributes, "Tags");
      //addInteger(attributes, "AnswerCount");
      //addInteger(attributes, "CommentCount");
      //addInteger(attributes, "FavoriteCount");

      if (postTypeId == 1) {
        stringBuilder.append(
            random.nextDouble() < engine.getEnvironment().getHoldOutProportion() ? "1" : "0");
      } else {
        stringBuilder.append("NULL");
      }

      stringBuilder.append(");\n");
    }
  };
  private SqlGenerator generatePostHistorySql = new SqlGenerator() {
    @Override
    public void apply(Attributes attributes) {
      stringBuilder.append("INSERT INTO PostHistory VALUES (");
      stringBuilder.append(siteId);
      stringBuilder.append(",");
      addInteger(attributes, "PostId");
      addInteger(attributes, "Id");
      addInteger(attributes, "PostHistoryTypeId");
      //addText(attributes, "RevisionGUID");
      addNumericDateTime(attributes, "CreationDate");
      //addInteger(attributes, "UserId");
      //addText(attributes, "UserDisplayName");
      //addText(attributes, "Comment");
      addText(attributes, "Text", true);
      addInteger(attributes, "CloseReasonId");
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      stringBuilder.append(");\n");
    }
  };
  private SqlGenerator generateCommentSql = new SqlGenerator() {
    @Override
    public void apply(Attributes attributes) {
      stringBuilder.append("INSERT INTO Comment VALUES (");
      stringBuilder.append(siteId);
      stringBuilder.append(",");
      addInteger(attributes, "PostId");
      addInteger(attributes, "Id");
      //addInteger(attributes, "Score");
      addText(attributes, "Text", true);
      //addNumericDateTime(attributes, "CreationDate");
      //addInteger(attributes, "UserId");
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
      stringBuilder.append(");\n");
    }
  };
  private final Engine<?, ?> engine;
  private final int siteId;
  private final Random random;
  private final StringBuilder stringBuilder = new StringBuilder("BEGIN;\n");
  private SqlGenerator sqlGenerator;
  private int count = 0;

  public XmlHandler(Engine<?, ?> engine, int siteId, long randomSeed) {
    this.engine = engine;
    this.siteId = siteId;
    this.random = new Random(randomSeed);
  }

  private void commit() throws SAXException {
    try {
      stringBuilder.append("COMMIT;\n");
      engine.getEnvironment().getDatabase().createStatement().executeUpdate(
          stringBuilder.toString());

      if (engine.isStopping()) {
        throw new ParseStoppedException();
      }

      count = 0;
      stringBuilder.setLength(0);
      stringBuilder.append("BEGIN;\n");
    } catch (Exception exception) {
      throw new SAXException(exception);
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    super.startElement(uri, localName, qName, attributes);

    if (qName.equalsIgnoreCase("row")) {
      try {
        sqlGenerator.apply(attributes);
      } catch (Exception exception) {
        throw new SAXException(exception);
      }

      count++;

      if (count == BATCH_SIZE) {
        commit();
      }
    } else if (qName.equalsIgnoreCase("posts")) {
      sqlGenerator = generatePostSql;
    } else if (qName.equalsIgnoreCase("posthistory")) {
      sqlGenerator = generatePostHistorySql;
    } else if (qName.equalsIgnoreCase("comments")) {
      sqlGenerator = generateCommentSql;
    }
  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
    commit();
  }
}