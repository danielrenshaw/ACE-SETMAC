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

import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ReadableDocument {

  private final String id;
  private final Object content;
  private final Map<String, Object> properties;

  public ReadableDocument(String id, Object content, Map<String, Object> properties) {
    this.id = id;
    this.content = content;
    this.properties = ImmutableMap.copyOf(properties);
  }

  public ReadableDocument(ReadWriteableDocument document) {
    this(document.getId(), document.getContent(), document.getProperties());
  }

  public String getId() {
    return id;
  }

  public Object getContent() {
    return content;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}