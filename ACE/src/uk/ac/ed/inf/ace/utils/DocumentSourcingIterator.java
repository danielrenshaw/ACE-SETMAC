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
import uk.ac.ed.inf.ace.DocType;
import uk.ac.ed.inf.ace.Processor;
import uk.ac.ed.inf.ace.ReadWriteableDocument;
import uk.ac.ed.inf.ace.ReadableDocument;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class DocumentSourcingIterator implements Iterator<ReadableDocument> {

  private final Iterator<String> documentIds;
  private final DocumentCache documentCache;
  private final DocType docType;
  private final Iterable<Processor> preCacheProcessors;
  private final Iterable<Processor> postCacheProcessors;
  private final String processorsKey;
  private final Random random;
  private ReadableDocument buffer;

  public DocumentSourcingIterator(Iterator<String> documentIds, DocumentCache documentCache,
      DocType docType, Iterable<Processor> preCacheProcessors,
      Iterable<Processor> postCacheProcessors, Random random) {
    this.documentIds = documentIds;
    this.documentCache = documentCache;
    this.docType = docType;
    this.preCacheProcessors = preCacheProcessors;
    this.postCacheProcessors = postCacheProcessors;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("-");
    stringBuilder.append(docType.getName());
    stringBuilder.append("-");

    for (Processor processor : preCacheProcessors) {
      stringBuilder.append("-");
      stringBuilder.append(processor.getCode());
    }

    this.processorsKey = stringBuilder.toString();
    this.random = random;
  }

  private ReadableDocument getNext() {
    try {
      ReadWriteableDocument document;

      do {
        if (documentIds.hasNext()) {
          String documentId = documentIds.next();

          try {
            document = new ReadWriteableDocument(documentCache.get(new DocumentCacheQuery(
                documentId + processorsKey, documentId, docType, preCacheProcessors, random)));

            for (Processor processor : postCacheProcessors) {
              if (document == null) {
                break;
              }

              document = processor.process(document, random);
            }
          } catch (NullPointerException nullPointerException) {
            document = null;
          }
        } else {
          return null;
        }
      } while (document == null);

      return new ReadableDocument(document);
    } catch (Exception exception) {
      throw new RuntimeException("Failed to source a document", exception);
    }
  }

  @Override
  public boolean hasNext() {
    if (buffer == null) {
      buffer = getNext();
      return buffer != null;
    } else {
      return true;
    }
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ReadableDocument next() {
    if (buffer == null) {
      return getNext();
    } else {
      ReadableDocument temp = buffer;
      buffer = null;
      return temp;
    }
  }
}