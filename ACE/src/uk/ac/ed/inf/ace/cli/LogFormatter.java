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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class LogFormatter extends Formatter {

  @Override
  public String format(LogRecord logRecord) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(logRecord.getMillis());
    String message = MessageFormat.format(logRecord.getMessage(), logRecord.getParameters());
    String level = logRecord.getLevel().getLocalizedName();
    Throwable throwable = logRecord.getThrown();

    if (level != null && level.length() > 5) {
      level = level.substring(0, 5);
    }

    if (throwable != null) {
      StringWriter stringWriter = null;
      PrintWriter printWriter = null;

      try {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        printWriter.print("\n");
        throwable.printStackTrace(printWriter);
        message += stringWriter.toString();
      } finally {
        if (printWriter != null) {
          printWriter.close();
        }
      }
    }

    return String.format(
        "%1$td-%1$tH:%1$tM:%1$tS %2$02d %3$5s %4$s %5$s\n",
        calendar,
        logRecord.getThreadID(),
        level,
        logRecord.getLoggerName(),
        message);
  }
}