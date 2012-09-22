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

import java.util.Random;
import uk.ac.ed.inf.ace.DocType;
import uk.ac.ed.inf.ace.Processor;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.ReadableDocument;

/**
 * Represents a query for a particular document from a document cache. Provides all of the
 * information needed to source the document if it is not present in the cache.
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DocumentCacheQuery {

  private final String key;
  private final String documentId;
  private final DocType docType;
  private final Iterable<Processor> processors;
  private final Random random;

  /**
   * @param key The unique identifier for the processed document.
   * @param documentId The unique identifier for the unprocessed document.
   * @param docType Provides a means of loading the document, if needed.
   * @param processors The set of processors that should be applied to a newly loaded document.
   */
  public DocumentCacheQuery(String key, String documentId, DocType docType,
      Iterable<Processor> processors, Random random) {
    this.key = key;
    this.documentId = documentId;
    this.docType = docType;
    this.processors = processors;
    this.random = random;
  }

  public String getDocumentId() {
    return documentId;
  }

  public DocType getDocType() {
    return docType;
  }

  public Iterable<Processor> getProcessors() {
    return processors;
  }

  public Random getRandom() {
    return random;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final DocumentCacheQuery other = (DocumentCacheQuery) obj;
    return key.equals(other.key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }
  
  public ReadableDocument getReadableDocument(ReadWriteableDocument document) {
    return new ReadableDocument(document);
  }
}