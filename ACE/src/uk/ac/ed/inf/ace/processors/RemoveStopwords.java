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
package uk.ac.ed.inf.ace.processors;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterators;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Set;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.ProcessorBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class RemoveStopwords
    extends ProcessorBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.RemoveStopwords> {

  private final Set<String> stopwords;

  public RemoveStopwords(Engine<?, ?> engine,
      uk.ac.ed.inf.ace.config.v1.RemoveStopwords config) throws Exception {
    super(engine, config);
    File file = new File(config.getPathname());
    Preconditions.checkState(file.exists());

    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    Builder<String> builder = ImmutableSet.builder();
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      line = line.trim();

      if (line.isEmpty()) {
        continue;
      }

      builder.add(line.toLowerCase());
    }

    stopwords = builder.build();
  }
  private final Predicate<String> IS_NOT_STOPWORD = new Predicate<String>() {
    @Override
    public boolean apply(String input) {
      return !stopwords.contains(input);
    }
  };

  @Override
  public ReadWriteableDocument process(ReadWriteableDocument document) {
    @SuppressWarnings("unchecked")
    Iterator<String> tokens = (Iterator<String>) document.getContent();
    document.setContent(Iterators.filter(tokens, IS_NOT_STOPWORD));
    return document;
  }
}