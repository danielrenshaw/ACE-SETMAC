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

import com.google.common.io.CharStreams;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.zip.InflaterInputStream;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class InflaterResultSet implements ResultSet, Wrapper {

  private final ResultSet delegate;

  public InflaterResultSet(ResultSet delegate) {
    this.delegate = delegate;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }

  @Override
  public boolean wasNull() throws SQLException {
    return delegate.wasNull();
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
    delegate.updateTimestamp(columnLabel, x);
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
    delegate.updateTimestamp(columnIndex, x);
  }

  @Override
  public void updateTime(String columnLabel, Time x) throws SQLException {
    delegate.updateTime(columnLabel, x);
  }

  @Override
  public void updateTime(int columnIndex, Time x) throws SQLException {
    delegate.updateTime(columnIndex, x);
  }

  @Override
  public void updateString(String columnLabel, String x) throws SQLException {
    delegate.updateString(columnLabel, x);
  }

  @Override
  public void updateString(int columnIndex, String x) throws SQLException {
    delegate.updateString(columnIndex, x);
  }

  @Override
  public void updateShort(String columnLabel, short x) throws SQLException {
    delegate.updateShort(columnLabel, x);
  }

  @Override
  public void updateShort(int columnIndex, short x) throws SQLException {
    delegate.updateShort(columnIndex, x);
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    delegate.updateSQLXML(columnLabel, xmlObject);
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    delegate.updateSQLXML(columnIndex, xmlObject);
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    delegate.updateRowId(columnLabel, x);
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    delegate.updateRowId(columnIndex, x);
  }

  @Override
  public void updateRow() throws SQLException {
    delegate.updateRow();
  }

  @Override
  public void updateRef(String columnLabel, Ref x) throws SQLException {
    delegate.updateRef(columnLabel, x);
  }

  @Override
  public void updateRef(int columnIndex, Ref x) throws SQLException {
    delegate.updateRef(columnIndex, x);
  }

  @Override
  public void updateObject(String columnLabel, Object x) throws SQLException {
    delegate.updateObject(columnLabel, x);
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
    delegate.updateObject(columnLabel, x, scaleOrLength);
  }

  @Override
  public void updateObject(int columnIndex, Object x) throws SQLException {
    delegate.updateObject(columnIndex, x);
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
    delegate.updateObject(columnIndex, x, scaleOrLength);
  }

  @Override
  public void updateNull(String columnLabel) throws SQLException {
    delegate.updateNull(columnLabel);
  }

  @Override
  public void updateNull(int columnIndex) throws SQLException {
    delegate.updateNull(columnIndex);
  }

  @Override
  public void updateNString(String columnLabel, String nString) throws SQLException {
    delegate.updateNString(columnLabel, nString);
  }

  @Override
  public void updateNString(int columnIndex, String nString) throws SQLException {
    delegate.updateNString(columnIndex, nString);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    delegate.updateNClob(columnLabel, reader);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    delegate.updateNClob(columnIndex, reader);
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    delegate.updateNClob(columnLabel, reader, length);
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    delegate.updateNClob(columnIndex, reader, length);
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    delegate.updateNClob(columnLabel, nClob);
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    delegate.updateNClob(columnIndex, nClob);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    delegate.updateNCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
    delegate.updateNCharacterStream(columnIndex, x);
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    delegate.updateNCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    delegate.updateNCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateLong(String columnLabel, long x) throws SQLException {
    delegate.updateLong(columnLabel, x);
  }

  @Override
  public void updateLong(int columnIndex, long x) throws SQLException {
    delegate.updateLong(columnIndex, x);
  }

  @Override
  public void updateInt(String columnLabel, int x) throws SQLException {
    delegate.updateInt(columnLabel, x);
  }

  @Override
  public void updateInt(int columnIndex, int x) throws SQLException {
    delegate.updateInt(columnIndex, x);
  }

  @Override
  public void updateFloat(String columnLabel, float x) throws SQLException {
    delegate.updateFloat(columnLabel, x);
  }

  @Override
  public void updateFloat(int columnIndex, float x) throws SQLException {
    delegate.updateFloat(columnIndex, x);
  }

  @Override
  public void updateDouble(String columnLabel, double x) throws SQLException {
    delegate.updateDouble(columnLabel, x);
  }

  @Override
  public void updateDouble(int columnIndex, double x) throws SQLException {
    delegate.updateDouble(columnIndex, x);
  }

  @Override
  public void updateDate(String columnLabel, Date x) throws SQLException {
    delegate.updateDate(columnLabel, x);
  }

  @Override
  public void updateDate(int columnIndex, Date x) throws SQLException {
    delegate.updateDate(columnIndex, x);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    delegate.updateClob(columnLabel, reader);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    delegate.updateClob(columnIndex, reader);
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    delegate.updateClob(columnLabel, reader, length);
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    delegate.updateClob(columnIndex, reader, length);
  }

  @Override
  public void updateClob(String columnLabel, Clob x) throws SQLException {
    delegate.updateClob(columnLabel, x);
  }

  @Override
  public void updateClob(int columnIndex, Clob x) throws SQLException {
    delegate.updateClob(columnIndex, x);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    delegate.updateCharacterStream(columnLabel, reader);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    delegate.updateCharacterStream(columnIndex, x);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    delegate.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    delegate.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader, int length)
      throws SQLException {
    delegate.updateCharacterStream(columnLabel, reader, length);
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
    delegate.updateCharacterStream(columnIndex, x, length);
  }

  @Override
  public void updateBytes(String columnLabel, byte[] x) throws SQLException {
    delegate.updateBytes(columnLabel, x);
  }

  @Override
  public void updateBytes(int columnIndex, byte[] x) throws SQLException {
    delegate.updateBytes(columnIndex, x);
  }

  @Override
  public void updateByte(String columnLabel, byte x) throws SQLException {
    delegate.updateByte(columnLabel, x);
  }

  @Override
  public void updateByte(int columnIndex, byte x) throws SQLException {
    delegate.updateByte(columnIndex, x);
  }

  @Override
  public void updateBoolean(String columnLabel, boolean x) throws SQLException {
    delegate.updateBoolean(columnLabel, x);
  }

  @Override
  public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    delegate.updateBoolean(columnIndex, x);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    delegate.updateBlob(columnLabel, inputStream);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    delegate.updateBlob(columnIndex, inputStream);
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream, long length)
      throws SQLException {
    delegate.updateBlob(columnLabel, inputStream, length);
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    delegate.updateBlob(columnIndex, inputStream, length);
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) throws SQLException {
    delegate.updateBlob(columnLabel, x);
  }

  @Override
  public void updateBlob(int columnIndex, Blob x) throws SQLException {
    delegate.updateBlob(columnIndex, x);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    delegate.updateBinaryStream(columnLabel, x);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    delegate.updateBinaryStream(columnIndex, x);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    delegate.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    delegate.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length)
      throws SQLException {
    delegate.updateBinaryStream(columnLabel, x, length);
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
    delegate.updateBinaryStream(columnIndex, x, length);
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
    delegate.updateBigDecimal(columnLabel, x);
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
    delegate.updateBigDecimal(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    delegate.updateAsciiStream(columnLabel, x);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    delegate.updateAsciiStream(columnIndex, x);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length)
      throws SQLException {
    delegate.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    delegate.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
    delegate.updateAsciiStream(columnLabel, x, length);
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
    delegate.updateAsciiStream(columnIndex, x, length);
  }

  @Override
  public void updateArray(String columnLabel, Array x) throws SQLException {
    delegate.updateArray(columnLabel, x);
  }

  @Override
  public void updateArray(int columnIndex, Array x) throws SQLException {
    delegate.updateArray(columnIndex, x);
  }

  @Override
  public void setFetchSize(int rows) throws SQLException {
    delegate.setFetchSize(rows);
  }

  @Override
  public void setFetchDirection(int direction) throws SQLException {
    delegate.setFetchDirection(direction);
  }

  @Override
  public boolean rowUpdated() throws SQLException {
    return delegate.rowUpdated();
  }

  @Override
  public boolean rowInserted() throws SQLException {
    return delegate.rowInserted();
  }

  @Override
  public boolean rowDeleted() throws SQLException {
    return delegate.rowDeleted();
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    return delegate.relative(rows);
  }

  @Override
  public void refreshRow() throws SQLException {
    delegate.refreshRow();
  }

  @Override
  public boolean previous() throws SQLException {
    return delegate.previous();
  }

  @Override
  public boolean next() throws SQLException {
    return delegate.next();
  }

  @Override
  public void moveToInsertRow() throws SQLException {
    delegate.moveToInsertRow();
  }

  @Override
  public void moveToCurrentRow() throws SQLException {
    delegate.moveToCurrentRow();
  }

  @Override
  public boolean last() throws SQLException {
    return delegate.last();
  }

  @Override
  public boolean isLast() throws SQLException {
    return delegate.isLast();
  }

  @Override
  public boolean isFirst() throws SQLException {
    return delegate.isFirst();
  }

  @Override
  public boolean isClosed() throws SQLException {
    return delegate.isClosed();
  }

  @Override
  public boolean isBeforeFirst() throws SQLException {
    return delegate.isBeforeFirst();
  }

  @Override
  public boolean isAfterLast() throws SQLException {
    return delegate.isAfterLast();
  }

  @Override
  public void insertRow() throws SQLException {
    delegate.insertRow();
  }

  @Override
  public SQLWarning getWarnings() throws SQLException {
    return delegate.getWarnings();
  }

  @Override
  @Deprecated
  public InputStream getUnicodeStream(String columnLabel) throws SQLException {
    return delegate.getUnicodeStream(columnLabel);
  }

  @Override
  @Deprecated
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    return delegate.getUnicodeStream(columnIndex);
  }

  @Override
  public URL getURL(String columnLabel) throws SQLException {
    return delegate.getURL(columnLabel);
  }

  @Override
  public URL getURL(int columnIndex) throws SQLException {
    return delegate.getURL(columnIndex);
  }

  @Override
  public int getType() throws SQLException {
    return delegate.getType();
  }

  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
    return delegate.getTimestamp(columnLabel, cal);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
    return delegate.getTimestamp(columnIndex, cal);
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return delegate.getTimestamp(columnLabel);
  }

  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return delegate.getTimestamp(columnIndex);
  }

  @Override
  public Time getTime(String columnLabel, Calendar cal) throws SQLException {
    return delegate.getTime(columnLabel, cal);
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal) throws SQLException {
    return delegate.getTime(columnIndex, cal);
  }

  @Override
  public Time getTime(String columnLabel) throws SQLException {
    return delegate.getTime(columnLabel);
  }

  @Override
  public Time getTime(int columnIndex) throws SQLException {
    return delegate.getTime(columnIndex);
  }

  @Override
  public String getString(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Must use getString(int).");
  }

  private String getInflatedBlob(int columnIndex) throws SQLException {
    int type = getMetaData().getColumnType(columnIndex);

    if (type != Types.BLOB) {
      return null;
    }

    byte[] bytes = getBytes(columnIndex);

    if (bytes == null) {
      return null;
    }

    try {
      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(
                byteArrayInputStream)) {
          try (InputStreamReader inputStreamReader = new InputStreamReader(inflaterInputStream,
                  Constants.UTF8_CHARSET)) {
            return CharStreams.toString(inputStreamReader);
          }
        }
      }
    } catch (IOException exception) {
      throw new SQLException(exception);
    }
  }

  @Override
  public String getString(int columnIndex) throws SQLException {
    String inflatedBlob = getInflatedBlob(columnIndex);

    if (inflatedBlob == null) {
      return delegate.getString(columnIndex);
    }

    return inflatedBlob;
  }

  @Override
  public Statement getStatement() throws SQLException {
    return delegate.getStatement();
  }

  @Override
  public short getShort(String columnLabel) throws SQLException {
    return delegate.getShort(columnLabel);
  }

  @Override
  public short getShort(int columnIndex) throws SQLException {
    return delegate.getShort(columnIndex);
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return delegate.getSQLXML(columnLabel);
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return delegate.getSQLXML(columnIndex);
  }

  @Override
  public RowId getRowId(String columnLabel) throws SQLException {
    return delegate.getRowId(columnLabel);
  }

  @Override
  public RowId getRowId(int columnIndex) throws SQLException {
    return delegate.getRowId(columnIndex);
  }

  @Override
  public int getRow() throws SQLException {
    return delegate.getRow();
  }

  @Override
  public Ref getRef(String columnLabel) throws SQLException {
    return delegate.getRef(columnLabel);
  }

  @Override
  public Ref getRef(int columnIndex) throws SQLException {
    return delegate.getRef(columnIndex);
  }

  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
    throw new UnsupportedOperationException("Must use getString(int).");
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
    String inflatedBlob = getInflatedBlob(columnIndex);

    if (inflatedBlob == null) {
      return delegate.getObject(columnIndex, map);
    }

    return inflatedBlob;
  }

  @Override
  public Object getObject(String columnLabel) throws SQLException {
    throw new UnsupportedOperationException("Must use getString(int).");
  }

  @Override
  public Object getObject(int columnIndex) throws SQLException {
    String inflatedBlob = getInflatedBlob(columnIndex);

    if (inflatedBlob == null) {
      return delegate.getObject(columnIndex);
    }

    return inflatedBlob;
  }

  @Override
  public String getNString(String columnLabel) throws SQLException {
    return delegate.getNString(columnLabel);
  }

  @Override
  public String getNString(int columnIndex) throws SQLException {
    return delegate.getNString(columnIndex);
  }

  @Override
  public NClob getNClob(String columnLabel) throws SQLException {
    return delegate.getNClob(columnLabel);
  }

  @Override
  public NClob getNClob(int columnIndex) throws SQLException {
    return delegate.getNClob(columnIndex);
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return delegate.getNCharacterStream(columnLabel);
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return delegate.getNCharacterStream(columnIndex);
  }

  @Override
  public ResultSetMetaData getMetaData() throws SQLException {
    return delegate.getMetaData();
  }

  @Override
  public long getLong(String columnLabel) throws SQLException {
    return delegate.getLong(columnLabel);
  }

  @Override
  public long getLong(int columnIndex) throws SQLException {
    return delegate.getLong(columnIndex);
  }

  @Override
  public int getInt(String columnLabel) throws SQLException {
    return delegate.getInt(columnLabel);
  }

  @Override
  public int getInt(int columnIndex) throws SQLException {
    return delegate.getInt(columnIndex);
  }

  @Override
  public int getHoldability() throws SQLException {
    return delegate.getHoldability();
  }

  @Override
  public float getFloat(String columnLabel) throws SQLException {
    return delegate.getFloat(columnLabel);
  }

  @Override
  public float getFloat(int columnIndex) throws SQLException {
    return delegate.getFloat(columnIndex);
  }

  @Override
  public int getFetchSize() throws SQLException {
    return delegate.getFetchSize();
  }

  @Override
  public int getFetchDirection() throws SQLException {
    return delegate.getFetchDirection();
  }

  @Override
  public double getDouble(String columnLabel) throws SQLException {
    return delegate.getDouble(columnLabel);
  }

  @Override
  public double getDouble(int columnIndex) throws SQLException {
    return delegate.getDouble(columnIndex);
  }

  @Override
  public Date getDate(String columnLabel, Calendar cal) throws SQLException {
    return delegate.getDate(columnLabel, cal);
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) throws SQLException {
    return delegate.getDate(columnIndex, cal);
  }

  @Override
  public Date getDate(String columnLabel) throws SQLException {
    return delegate.getDate(columnLabel);
  }

  @Override
  public Date getDate(int columnIndex) throws SQLException {
    return delegate.getDate(columnIndex);
  }

  @Override
  public String getCursorName() throws SQLException {
    return delegate.getCursorName();
  }

  @Override
  public int getConcurrency() throws SQLException {
    return delegate.getConcurrency();
  }

  @Override
  public Clob getClob(String columnLabel) throws SQLException {
    return delegate.getClob(columnLabel);
  }

  @Override
  public Clob getClob(int columnIndex) throws SQLException {
    return delegate.getClob(columnIndex);
  }

  @Override
  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return delegate.getCharacterStream(columnLabel);
  }

  @Override
  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return delegate.getCharacterStream(columnIndex);
  }

  @Override
  public byte[] getBytes(String columnLabel) throws SQLException {
    return delegate.getBytes(columnLabel);
  }

  @Override
  public byte[] getBytes(int columnIndex) throws SQLException {
    return delegate.getBytes(columnIndex);
  }

  @Override
  public byte getByte(String columnLabel) throws SQLException {
    return delegate.getByte(columnLabel);
  }

  @Override
  public byte getByte(int columnIndex) throws SQLException {
    return delegate.getByte(columnIndex);
  }

  @Override
  public boolean getBoolean(String columnLabel) throws SQLException {
    return delegate.getBoolean(columnLabel);
  }

  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    return delegate.getBoolean(columnIndex);
  }

  @Override
  public Blob getBlob(String columnLabel) throws SQLException {
    return delegate.getBlob(columnLabel);
  }

  @Override
  public Blob getBlob(int columnIndex) throws SQLException {
    return delegate.getBlob(columnIndex);
  }

  @Override
  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return delegate.getBinaryStream(columnLabel);
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return delegate.getBinaryStream(columnIndex);
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return delegate.getBigDecimal(columnLabel);
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return delegate.getBigDecimal(columnIndex);
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
    return delegate.getBigDecimal(columnLabel, scale);
  }

  @Override
  @Deprecated
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    return delegate.getBigDecimal(columnIndex, scale);
  }

  @Override
  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return delegate.getAsciiStream(columnLabel);
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return delegate.getAsciiStream(columnIndex);
  }

  @Override
  public Array getArray(String columnLabel) throws SQLException {
    return delegate.getArray(columnLabel);
  }

  @Override
  public Array getArray(int columnIndex) throws SQLException {
    return delegate.getArray(columnIndex);
  }

  @Override
  public boolean first() throws SQLException {
    return delegate.first();
  }

  @Override
  public int findColumn(String columnLabel) throws SQLException {
    return delegate.findColumn(columnLabel);
  }

  @Override
  public void deleteRow() throws SQLException {
    delegate.deleteRow();
  }

  @Override
  public void close() throws SQLException {
    delegate.close();
  }

  @Override
  public void clearWarnings() throws SQLException {
    delegate.clearWarnings();
  }

  @Override
  public void cancelRowUpdates() throws SQLException {
    delegate.cancelRowUpdates();
  }

  @Override
  public void beforeFirst() throws SQLException {
    delegate.beforeFirst();
  }

  @Override
  public void afterLast() throws SQLException {
    delegate.afterLast();
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    return delegate.absolute(row);
  }

  @Override
  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return delegate.getObject(columnIndex, type);
  }

  @Override
  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return delegate.getObject(columnLabel, type);
  }
}