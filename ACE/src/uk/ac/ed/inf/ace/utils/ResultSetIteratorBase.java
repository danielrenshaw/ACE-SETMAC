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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class ResultSetIteratorBase<T> implements Iterator<T> {

  private final ResultSet resultSet;
  private T buffer;

  protected ResultSetIteratorBase(ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  @Override
  public boolean hasNext() {
    if (buffer == null) {
      try {
        buffer = next();
        return true;
      } catch (NoSuchElementException exception) {
        return false;
      }
    } else {
      return true;
    }
  }

  protected abstract T get(ResultSet resultSet) throws SQLException;

  @Override
  public T next() {
    if (buffer == null) {
      try {
        if (resultSet.next()) {
          return get(resultSet);
        } else {
          resultSet.close();
          throw new NoSuchElementException();
        }
      } catch (SQLException sqlException) {
        throw new RuntimeException("Failed to extract next row.", sqlException);
      }
    } else {
      T document = buffer;
      buffer = null;
      return document;
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}