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
package uk.ac.ed.inf.ace.cli;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class OutStream extends PrintStream {

  private final PrintStream realOut;
  private String prompt = "";
  private boolean proxyEnabled = true;

  public OutStream(PrintStream out) {
    super(out);
    this.realOut = out;
  }

  private void undoInput() {
    if (proxyEnabled) {
      proxyEnabled = false;
      realOut.print(Strings.repeat("\b", prompt.length()));
    }
  }

  private void redoInput() {
    if (!proxyEnabled) {
      realOut.print(prompt);
      proxyEnabled = true;
    }
  }

  @Override
  public synchronized void write(int b) {
    undoInput();
    super.write(b);
    redoInput();
  }

  @Override
  public synchronized void write(byte[] buf, int off, int len) {
    undoInput();
    super.write(buf, off, len);
    redoInput();
  }

  @Override
  public synchronized void write(byte[] b) throws IOException {
    undoInput();
    super.write(b);
    redoInput();
  }

  public synchronized void printPrompt(String value) {
    prompt = value;
    realOut.print(prompt);
  }
}