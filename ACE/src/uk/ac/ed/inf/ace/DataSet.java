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

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public interface DataSet extends Named {

  Iterator<ReadableDocument> getTrainingDocuments(RandomSource randomSource, int fold, int folds,
      Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors,
      DocType docType) throws Exception;

  Iterator<ReadableDocument> getValidationDocuments(RandomSource randomSource, int fold, int folds,
      Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors)
      throws Exception;

  Iterator<ReadableDocument> getTestDocuments(RandomSource randomSource,
      Iterable<Processor> preCacheProcessors, Iterable<Processor> postCacheProcessors)
      throws Exception;
}