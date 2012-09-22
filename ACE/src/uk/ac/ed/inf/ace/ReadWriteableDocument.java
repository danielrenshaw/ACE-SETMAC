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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.MapMaker;
import java.util.Map;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ReadWriteableDocument {

  private final String id;
  private Object content;
  private final Map<String, Object> properties = new MapMaker().concurrencyLevel(1).makeMap();

  public ReadWriteableDocument(String id, Object content) {
    Preconditions.checkNotNull(id);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(id.trim()));
    this.id = id;
    this.content = content;
  }

  public ReadWriteableDocument(ReadableDocument document) {
    this(document.getId(), document.getContent());
    this.properties.putAll(document.getProperties());
  }

  public String getId() {
    return id;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public synchronized Object getContent() {
    return content;
  }

  public synchronized void setContent(Object value) {
    this.content = value;
  }
}