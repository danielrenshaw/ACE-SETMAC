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
package uk.ac.ed.inf.setmac.databases;

import java.io.IOException;
import java.io.InputStream;

/**
 * Used when reading the XML files provided by StackExchange to fix a problem that exists in some of
 * those files - the XML is not well formed due to entities encoding new line characters which are
 * not permitted by the XML specification. Maintains a 6 character buffer and only emits characters
 * once it is determined they cannot be a part of an invalid entity: &amp;#xB &amp;#xC &amp;#x1A
 * &amp;#x1B. The entity is assumed to be complete once one of the 4 strings above is found followed
 * by any of the following characters: ; " &gt; &lt; &amp; (i.e. it does not require a properly
 * encoded entity - anything that looks like an entity will be treated as one too).
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class RemoveBadCharactersInputStream extends InputStream {

  private final InputStream in;
  private final int[] buffer = new int[6];
  private boolean filled = false;
  private boolean moreToRead = true;
  // The position within the buffer containing the next character to be emitted.
  private int startIndex = 0;
  private int matchIndex = 0;
  private boolean isLong = false;

  public RemoveBadCharactersInputStream(InputStream in) {
    this.in = in;
  }

  private void fillBuffer() throws IOException {
    if (!moreToRead) {
      return;
    }

    filled = false;
    startIndex = 0;
    matchIndex = 0;
    isLong = false;
    int next = -1;

    while (!filled && startIndex < buffer.length && (next = in.read()) >= 0) {
      buffer[startIndex++] = next;
      match(next);
    }

    if (!filled) {
      moreToRead = next >= 0;
      startIndex = 0;
      filled = true;
    }
  }

  /**
   * Advances the match state given a new character.
   *
   * @param next The character to be matched.
   */
  private void match(int next) throws IOException {
    if ((matchIndex == 0 && next == '&')
        || (matchIndex == 1 && next == '#')
        || (matchIndex == 2 && next == 'x')
        || (matchIndex == 3 && (next == 'B' || next == 'C' || next == '1'))
        || (matchIndex == 4 && isLong && (next == 'A' || next == 'B'))) {
      // The character continues an incomplete invalid entity

      if (matchIndex == 3 && next == '1') {
        // Determines whether the entity might be a "long" one (i.e. one that involves two
        // hexadecimal characters
        isLong = true;
      }

      matchIndex++;
    } else if ((matchIndex == 4 && !isLong && (next == ';' || next == '"'
        || next == '>' || next == '<' || next == '&'))
        || (matchIndex == 5 && isLong && (next == ';' || next == '"'
        || next == '>' || next == '<' || next == '&'))) {
      // The character completes an invalid entity which can now be discarded and the buffer
      // refilled
      fillBuffer();
    } else {
      // The character does not continue any partial invalid entity match so reset the state back to
      // the start
      matchIndex = 0;
      isLong = false;
    }
  }

  @Override
  public int read() throws IOException {
    if (filled) {
      if (moreToRead) {
        // We can overwrite the previous character which has already been emitted
        int index = (startIndex == 0 ? buffer.length : startIndex) - 1;
        int next = in.read();
        buffer[index] = next;
        moreToRead = next >= 0;
        match(next);
      } else if (buffer[startIndex] < 0) {
        // If the end of stream has been reached just return -1 from now on
        return -1;
      }
    } else {
      // Nothing in the buffer so we better fill it to see if we're starting an invalid entity
      assert moreToRead = true;
      assert startIndex == 0;
      assert matchIndex == 0;
      assert isLong = false;
      fillBuffer();
      filled = true;
    }

    int next = buffer[startIndex];

    // If we've reached the end of the buffer, cycle back to the beginning, else advance the pointer
    if (startIndex == (buffer.length - 1)) {
      startIndex = 0;
    } else {
      startIndex++;
    }

    return next;
  }
}