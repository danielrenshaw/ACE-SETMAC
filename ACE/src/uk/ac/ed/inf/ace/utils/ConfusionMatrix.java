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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.util.Iterator;
import java.util.Set;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix.Result;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ConfusionMatrix implements Iterable<Result> {

  public static class Result {

    private final Object actualLabel;
    private final Object predictedLabel;
    private final Integer frequency;

    private Result(Cell<Object, Object, Integer> cell) {
      this.actualLabel = cell.getRowKey();
      this.predictedLabel = cell.getColumnKey();
      this.frequency = cell.getValue();
    }

    public Result(Object actualLabel, Object predictedLabel, int frequency) {
      assert actualLabel.getClass().equals(predictedLabel.getClass());
      this.actualLabel = actualLabel;
      this.predictedLabel = predictedLabel;
      this.frequency = frequency;
    }

    public Object getActualLabel() {
      return actualLabel;
    }

    public Integer getFrequency() {
      return frequency;
    }

    public Object getPredictedLabel() {
      return predictedLabel;
    }
  }
  private final Table<Object, Object, Integer> table = HashBasedTable.create();

  public void add(Object actualLabel, Object predictedLabel, int frequency) {
    assert actualLabel.getClass().equals(predictedLabel.getClass());
    Integer value = table.get(actualLabel, predictedLabel);

    if (value == null) {
      value = frequency;
    } else {
      value += frequency;
    }

    table.put(actualLabel, predictedLabel, value);
  }

  public void add(Result result) {
    add(result.getActualLabel(), result.getPredictedLabel(), result.getFrequency());
  }

  public Integer get(Object actualLabel, Object predictedLabel) {
    assert actualLabel.getClass().equals(predictedLabel.getClass());
    return table.get(actualLabel, predictedLabel);
  }

  public Set<Object> getActualLabels() {
    return table.rowKeySet();
  }

  public Set<Object> getPredictedLabels() {
    return table.columnKeySet();
  }

  @Override
  public Iterator<Result> iterator() {
    return new Iterator<Result>() {
      private final Iterator<Cell<Object, Object, Integer>> cells =
          table.cellSet().iterator();

      @Override
      public boolean hasNext() {
        return cells.hasNext();
      }

      @Override
      public Result next() {
        return new Result(cells.next());
      }

      @Override
      public void remove() {
        cells.remove();
      }
    };
  }
}