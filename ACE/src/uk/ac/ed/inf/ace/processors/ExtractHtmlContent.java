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

import org.jsoup.Jsoup;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.ProcessorBase;
import uk.ac.ed.inf.ace.ReadWriteableDocument;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ExtractHtmlContent
    extends ProcessorBase<Engine<?, ?>, uk.ac.ed.inf.ace.config.v1.Processor> {

  public ExtractHtmlContent(Engine<?, ?> engine, uk.ac.ed.inf.ace.config.v1.Processor config) {
    super(engine, config);
  }

  @Override
  public ReadWriteableDocument process(ReadWriteableDocument document) {
    org.jsoup.nodes.Document jsoup = Jsoup.parseBodyFragment((String) document.getContent());
    document.setContent(jsoup.text());
    return document;
  }
}