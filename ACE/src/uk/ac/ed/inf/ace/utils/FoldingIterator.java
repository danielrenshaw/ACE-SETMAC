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

import java.util.Iterator;
import java.util.Random;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class FoldingIterator<T> extends RandomConditionIterator<T> {

  private final int folds;
  private final int fold;
  private final boolean inFold;

  public FoldingIterator(Iterator<T> source, long randomSeed, int folds, int fold, boolean inFold) {
    super(source, randomSeed);
    this.folds = folds;
    this.fold = fold;
    this.inFold = inFold;
  }

  @Override
  protected boolean condition(T item, Random random) {
    return folds == 1 || (random.nextInt(folds) == fold) == inFold;
  }
}