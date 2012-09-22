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
package uk.ac.ed.inf.ace;

import java.util.Iterator;
import java.util.Random;
import uk.ac.ed.inf.ace.utils.DocumentSourcingIterator;
import uk.ac.ed.inf.ace.utils.FoldingIterator;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class DataSetBase<E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.DataSetBase>
    extends NamedBase<E, C> implements DataSet {

  public DataSetBase(E engine, C config) {
    super(engine, config);
  }

  protected abstract Iterator<String> getDocumentIds(boolean forTest, Random random)
      throws Exception;

  private Iterator<String> getDocumentIds(Random random, int fold, int folds, boolean inFold,
      boolean forTest) throws Exception {
    return new FoldingIterator<>(getDocumentIds(forTest, random), random.nextLong(), folds, fold,
        inFold);
  }

  private Iterator<ReadableDocument> getDocuments(RandomSource randomSource, int fold, int folds,
      boolean inFold, boolean forTest, final Iterable<Processor> preCacheProcessors,
      Iterable<Processor> postCacheProcessors, final DocType docType) throws Exception {
    final Random random = randomSource.get(this);
    Iterator<String> documentIds = getDocumentIds(random, fold, folds, inFold, forTest);
    return new DocumentSourcingIterator(documentIds, getEngine().getDocumentCache(), docType,
        preCacheProcessors, postCacheProcessors, random);
  }

  @Override
  public Iterator<ReadableDocument> getTrainingDocuments(RandomSource randomSource, int fold,
      int folds, Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors,
      DocType docType) throws Exception {
    return getDocuments(randomSource, fold, folds, false, false, preCacheProcessors,
        postCacheProcessors, docType);
  }

  @Override
  public Iterator<ReadableDocument> getValidationDocuments(RandomSource randomSource, int fold,
      int folds, Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors)
      throws Exception {
    return getDocuments(randomSource, fold, folds, true, false, preCacheProcessors,
        postCacheProcessors, getEngine().getEnvironment().getClassificationDocType());
  }

  @Override
  public Iterator<ReadableDocument> getTestDocuments(RandomSource randomSource,
      Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors)
      throws Exception {
    return getDocuments(randomSource, 1, 1, true, true, preCacheProcessors, postCacheProcessors,
        getEngine().getEnvironment().getClassificationDocType());
  }
}