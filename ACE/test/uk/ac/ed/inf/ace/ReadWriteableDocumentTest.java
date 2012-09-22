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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class ReadWriteableDocumentTest {

  @Test
  public void testGettersNoModification() {
    String id = "abc";
    String content = "def";
    ReadWriteableDocument instance = new ReadWriteableDocument(id, content);
    assertEquals(id, instance.getId());
    assertEquals(content, instance.getContent());
  }

  @Test
  public void testIdNormalisation() {
    String id = " a b c ";
    ReadWriteableDocument instance = new ReadWriteableDocument(id, null);
    assertEquals(" a b c ", instance.getId());
  }

  @Test(expected = NullPointerException.class)
  public void testNullId() {
    ReadWriteableDocument document = new ReadWriteableDocument(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyId() {
    ReadWriteableDocument document = new ReadWriteableDocument("", null);
  }
}