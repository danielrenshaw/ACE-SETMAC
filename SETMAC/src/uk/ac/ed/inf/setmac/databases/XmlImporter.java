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

import com.google.common.base.Preconditions;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import uk.ac.ed.inf.ace.Database;
import uk.ac.ed.inf.ace.utils.StoppingException;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class XmlImporter {

  private final Engine engine;

  public XmlImporter(Engine engine) {
    this.engine = engine;
  }

  public void importXml(File file, String site, long randomSeed)
      throws Exception {
    Preconditions.checkNotNull(file);
    Preconditions.checkState(file.exists(), "XML file does not exist: {0}", file);
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    InputStream inputStream = new RemoveBadCharactersInputStream(
        new BufferedInputStream(new FileInputStream(file)));
    Database database = engine.getEnvironment().getDatabase();
    int siteId = DB.getSiteId(database, site);
    XmlHandler handler = new XmlHandler(engine, siteId, randomSeed);

    try {
      parser.parse(inputStream, handler);
    } catch (SAXException saxException) {
      if (saxException instanceof ParseStoppedException) {
        throw new StoppingException();
      } else {
        Throwable temp = saxException.getException();

        while (true) {
          if (temp == null) {
            break;
          }

          if (temp instanceof ParseStoppedException) {
            throw new StoppingException();
          }

          temp = temp.getCause();
        }
      }

      throw saxException;
    }
  }
}